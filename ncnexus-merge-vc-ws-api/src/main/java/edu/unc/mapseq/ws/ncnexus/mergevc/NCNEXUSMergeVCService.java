package edu.unc.mapseq.ws.ncnexus.mergevc;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.BindingType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@WebService(targetNamespace = "http://mergevc.ncnexus.ws.mapseq.unc.edu", serviceName = "NCNEXUSMergeVCService", portName = "NCNEXUSMergeVCPort")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Path("/NCNEXUSMergeVCService/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface NCNEXUSMergeVCService {

    @GET
    @Path("/getMetrics/{subjectName}")
    @WebMethod
    public List<MetricsResult> getMetrics(@PathParam("subjectName") @WebParam(name = "subjectName") String subjectName);

}
