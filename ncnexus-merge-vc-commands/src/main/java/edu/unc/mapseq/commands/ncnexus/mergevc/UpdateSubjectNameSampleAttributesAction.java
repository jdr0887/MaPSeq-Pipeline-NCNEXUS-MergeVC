package edu.unc.mapseq.commands.ncnexus.mergevc;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "ncnexus-mergevc", name = "update-subject-name-sample-attributes", description = "Update subjectName Sample Attributes")
@Service
public class UpdateSubjectNameSampleAttributesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(UpdateSubjectNameSampleAttributesAction.class);

    @Option(name = "--dryRun", description = "Don't persist anything", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    public UpdateSubjectNameSampleAttributesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");
        FlowcellDAO flowcellDAO = maPSeqDAOBeanService.getFlowcellDAO();
        SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();

        try {

            List<String> lines = IOUtils
                    .readLines(getClass().getClassLoader().getResourceAsStream("edu/unc/mapseq/commands/nec/mergebam/SubjectToBarcodeMap.csv"));

            List<Flowcell> flowcells = flowcellDAO.findByStudyName("NCNEXUS");

            if (CollectionUtils.isNotEmpty(flowcells)) {

                for (Flowcell flowcell : flowcells) {

                    List<Sample> samples = sampleDAO.findByFlowcellId(flowcell.getId());

                    if (CollectionUtils.isNotEmpty(samples)) {

                        for (Sample sample : samples) {

                            if ("Undetermined".equals(sample.getBarcode())) {
                                continue;
                            }

                            String subjectName = null;

                            // find subjectName
                            for (String line : lines) {
                                if (line.contains(flowcell.getName()) && line.contains(sample.getName())) {
                                    subjectName = line.split(",")[0];
                                    break;
                                }
                            }

                            if (StringUtils.isEmpty(subjectName)) {
                                continue;
                            }

                            logger.info(flowcell.toString());
                            logger.info(sample.toString());
                            logger.info("subjectName: {}", subjectName);

                            if (!dryRun) {

                                Set<Attribute> attributes = sample.getAttributes();

                                Set<String> attributeNameSet = new HashSet<String>();
                                for (Attribute attribute : attributes) {
                                    attributeNameSet.add(attribute.getName());
                                }

                                if (!attributeNameSet.contains("subjectName")) {
                                    Attribute attribute = new Attribute("subjectName", subjectName);
                                    attributes.add(attribute);
                                    maPSeqDAOBeanService.getSampleDAO().save(sample);
                                } else {
                                    for (Attribute attribute : attributes) {
                                        if ("subjectName".equals(attribute.getName())) {
                                            attribute.setValue(subjectName);
                                            maPSeqDAOBeanService.getAttributeDAO().save(attribute);
                                            break;
                                        }
                                    }
                                }

                            }

                        }

                    }

                }

            }

        } catch (MaPSeqDAOException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

}
