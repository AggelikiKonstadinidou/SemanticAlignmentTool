package org.cloud4All;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.cloud4All.IPR.DiscountSchema;
import org.cloud4All.IPR.EULA;
import org.cloud4All.IPR.PerCountryReputation;
import org.cloud4All.IPR.PerCountrySolutionUsage;
import org.cloud4All.IPR.PerVendorSolutionUsage;
import org.cloud4All.IPR.SLA;
import org.cloud4All.IPR.SolutionLicense;
import org.cloud4All.IPR.SolutionUserFeedback;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Element;
import org.cloud4All.ontology.OntologyInstance;
import org.cloud4All.ontology.RegistryTerm;
import org.cloud4All.ontology.RegistryTermForJSON;
import org.cloud4All.ontology.Utils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.gson.Gson;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "fileDownloadController")
@SessionScoped
public class FileDownloadController {

	static private String FILENAME = "cloud4All_settings.json";
	static private String FILENAME_SOLUTIONS = "cloud4All_solutions.json";
	private StreamedContent file;
	private OntologyInstance ontologyInstance;

	public FileDownloadController() {

	}

	public StreamedContent getFile() {
		return file;
	}
	
	public void writeGsonAndExportFile(String fileName, String json) throws IOException{
		
		//1. write json to a new file
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(json);
		out.close();

		File fil = new File(fileName);

		InputStream stream = new FileInputStream(fil);
		file = new DefaultStreamedContent(stream, "text/txt", fileName);

		File localfile = new File(fileName);
		FileInputStream fis = new FileInputStream(localfile);

		// 2. get Liferay's ServletResponse
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();

		// 3. write the file into the outputStream
		OutputStream outputStream = res.getOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = fis.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}
	
		outputStream.close();
		fis.close();
		
		
	}
	
	public void exportJsonSchema(String filename,boolean settingsFlag){
		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);

			JsonArray allSolutions = new JsonArray();
			JsonObject solution;
			JsonObject contexts;
			JsonArray contextsArray;
			JsonObject contextInnerObj;
			JsonArray settingsHandlers;
			JsonObject settingsInnerObj;
			JsonObject optionObj;
			JsonObject lifecycleManager;
			JsonArray startArray;
			JsonArray stopArray;
			JsonObject startObj;
			JsonObject stopObj;
			JsonArray capabilities;
			JsonObject capabilitiesTransformations;
			
			Gson gson = new GsonBuilder().setPrettyPrinting()
					.disableHtmlEscaping().serializeNulls()
					.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
					.create();
			List<Solution> allSolutionsList = ontologyInstance.getSolutions();
			allSolutionsList.addAll(ontologyInstance.getServices());

			for (Solution sol : allSolutionsList) {

				// application info
				solution = new JsonObject();
				solution.addProperty("name", sol.getName());
				
				if (!sol.getId().isEmpty())
					solution.addProperty("id", sol.getId());

				// contexts info
				contexts = new JsonObject();
				contextsArray = new JsonArray();
				contextInnerObj = new JsonObject();
				
				for (ETNACluster cl : sol.getEtnaProperties()) {
					if (cl.getName().equals("Operating Systems")) {
						for (String s : cl.getSelectedProperties()) {
							contextInnerObj.addProperty("id", s);
							contextsArray.add(contextInnerObj);
							contextInnerObj = new JsonObject();
						}

					}
				}
				
				contexts.add("OS", contextsArray);

				// settingHandlers
				settingsHandlers = new JsonArray();
				settingsInnerObj = new JsonObject();
				settingsInnerObj.addProperty("type",
						Utilities.convertSelectedHandlerType(sol.getHandlerType()));

				// the option object is filled with
				// attributes according to the type
				optionObj = new JsonObject();
				boolean flag = false;
				JsonObject extraObject = new JsonObject();
				boolean extraFlag = false;
				String title = "";
				int size = sol.getOptions().size();

				if (sol.getHandlerType().equals("Registry")) {

					for (int i = 0; i < sol.getOptions().size(); i++) {
						Element el = sol.getOptions().get(i);
						if (!el.getOptionValue().isEmpty()
								&& !el.getOptionName().equals("HKey")
								&& !el.getOptionName().equals("Path")) {
							extraObject.addProperty(el.getOptionName(),
									el.getOptionValue());
							extraFlag = true;
						} else if (!el.getOptionValue().isEmpty()
								&& (el.getOptionName().equals("HKey") || el
										.getOptionName().equals("Path"))) {
							optionObj.addProperty(el.getOptionName(),
									el.getOptionValue());
						}
					}

					title = "datatypes";
			

				} else if (sol.getHandlerType().equals("Spi")) {

					for (int i = 0; i < sol.getOptions().size(); i++) {
						Element el = sol.getOptions().get(i);
						
						if (!el.getOptionValue().isEmpty()
								&& (el.getOptionName().equals("Type"))
								|| el.getOptionName().equals("Name")) {
							
							extraObject.addProperty(el.getOptionName(),
									el.getOptionValue());
							extraFlag = true;
							
						}else if(!el.getOptionValue().isEmpty()
								&& !el.getOptionName().equals("Type")
								&& !el.getOptionName().equals("Name")){
							
							optionObj.addProperty(el.getOptionName(),
									el.getOptionValue());
						}
					}

					title = "pvParam";
					

				} else if (sol.getHandlerType().equals("Xml") && sol.getOptions().size()>0) {

					JsonObject extraInnerObj = new JsonObject();
					boolean newFlag = false;
					
					int index = -1;
					index = sol.getOptions().indexOf("Map");

					if (index != -1) {

						if (sol.getOptions().get(index).getOptionValue()
								.isEmpty())
							extraObject.addProperty("Map", sol.getOptions()
									.get(index).getOptionValue());
						extraFlag = true;
					}

					for (int i = 0; i < sol.getOptions().size(); i++) {
						Element el = sol.getOptions().get(i);
						if (!el.getOptionValue().isEmpty()
								&& (el.getOptionName().equalsIgnoreCase("Key")
										|| el.getOptionName().equalsIgnoreCase(
												"Input Path") || el
										.getOptionName().equalsIgnoreCase("Name"))) {

							extraInnerObj.addProperty(el.getOptionName(),
									el.getOptionValue());
							newFlag = true;
							
						} else if (!el.getOptionValue().isEmpty()
								&& !el.getOptionName().equalsIgnoreCase("Key")
								&& !el.getOptionName().equalsIgnoreCase("Input Path")
								&& !el.getOptionName().equalsIgnoreCase("Name")
								&& !el.getOptionName().equalsIgnoreCase("Map")) {

							optionObj.addProperty(el.getOptionName(),
									el.getOptionValue());
						}
					}

					if (newFlag)
						extraObject.add("mapString", extraInnerObj);

					title = "rules";
					

				}else{
					
					for (int i = 0; i < size; i++) {

						Element el = sol.getOptions().get(i);

						if (!el.getOptionValue().isEmpty()) {

							if (el.getOptionValue().equals("true")
									|| el.getOptionValue().equals("false"))
								optionObj.addProperty(el.getOptionName(),
										el.isBooleanValue());
							else
								optionObj.addProperty(el.getOptionName(),
										el.getOptionValue());
								
							flag = true;
						}
					}
				}

				

				if (extraFlag)
					optionObj.add(title, extraObject);

				if (flag || extraFlag || sol.getOptions().size()>0)
					settingsInnerObj.add("options", optionObj);

				if (!sol.getHandlerType().isEmpty())
					settingsHandlers.add(settingsInnerObj);
				
				// capabilities
				capabilities = new JsonArray();
				JsonPrimitive el = null;

				if (!sol.getCapabilities().isEmpty()) {
					el = new JsonPrimitive(sol.getCapabilities().replace(
							"\r\n", ""));
					capabilities.add(el);
					settingsInnerObj.add("capabilities", capabilities);
				}

				// capabilitiesTransformations
				capabilitiesTransformations = new JsonObject();

				if (!sol.getCapabilitiesTransformations().isEmpty()) {

					el = new JsonPrimitive("MYITEM"
							+ sol.getCapabilitiesTransformations()
									.replace("\r\n", "").replace("\n", "")
							+ "MYITEM");
					settingsInnerObj.add("capabilitiesTransformations", el);
				}

				// lifecycleManager
				lifecycleManager = new JsonObject();
				// start
				startArray = new JsonArray();

				JsonPrimitive element = new JsonPrimitive("setSettings");
				startArray.add(element);

				startObj = new JsonObject();

				if (!sol.getStartCommand().isEmpty()) {

					if (sol.getStartCommand().contains("AND")) {
						String[] splitted = sol.getStartCommand().split("AND");
						startObj.addProperty("type", "gpii.launch.exec");
						startObj.addProperty("command", splitted[0]);
						startArray.add(startObj);
						
						startObj = new JsonObject();
						startObj.addProperty("type", "gpii.launch.exec");
						startObj.addProperty("command", splitted[1]);
						startArray.add(startObj);

					} else {
						startObj.addProperty("type", "gpii.launch.exec");
						startObj.addProperty("command", sol.getStartCommand());
						startArray.add(startObj);
					}
				}

				lifecycleManager.add("start", startArray);

				// stop
				stopArray = new JsonArray();
				stopObj = new JsonObject();

				if (!sol.getStopCommand().isEmpty()) {
					
					if (sol.getStopCommand().contains("AND")) {
						String[] splitted = sol.getStopCommand().split("AND");
						stopObj.addProperty("type", "gpii.launch.exec");
						stopObj.addProperty("command", splitted[0]);
						stopArray.add(stopObj);

						stopObj = new JsonObject();
						stopObj.addProperty("type", "gpii.launch.exec");
						stopObj.addProperty("command", splitted[1]);
						stopArray.add(stopObj);

					} else {
						stopObj.addProperty("type", "gpii.launch.exec");
						stopObj.addProperty("command", sol.getStopCommand());
						stopArray.add(stopObj);
					}
				}

				element = new JsonPrimitive("restoreSettings");
				stopArray.add(element);
				lifecycleManager.add("stop", stopArray);

				solution.add("contexts", contexts);
				
				if (!sol.getHandlerType().isEmpty())
					solution.add("settingHandlers", settingsHandlers);
				
				
				
				solution.add("lifecycleManager", lifecycleManager);
				
				if(settingsFlag){
					JsonArray settings = new JsonArray();
					JsonObject setObj =  null;
					for(Setting set : sol.getSettings()){
						setObj = set.getJsonSchema();
						settings.add(setObj);
					}
					
					solution.add("settings",settings);
				}
				
				

				allSolutions.add(solution);

			}

			writeGsonAndExportFile(
					filename,
					gson.toJson(allSolutions).replace("\\\\", "\\")
							.replace("\\\"", "\"").replace("\r\n", "")
							.replace("\"MYITEM", "").replace("MYITEM\"", "")
							.replace("[\"", "").replace("\"]", ""));
					
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public static void main(String args[]){
//		String string = "{\"HighContrastOn\": {\"transform\": {\"type\": \"fluid.transforms.value\","
//				+ "\"inputPath\": \"display.screenEnhancement.-provisional-highContrastEnabled\","
//				+ "\"outputPath\": \"value\"},"
//				+ "\"path\": {\"transform\": {\"type\": \"fluid.transforms.literalValue\","
//				+ "\"value\": \"pvParam.dwFlags.HCF_HIGHCONTRASTON\"}}}}";
		
//		String string = "{"
//				+ "\"General\\.iMagnification\": \"http://registry\\.gpii\\.net/common/magnification\","
//				+ "\"General\\.InvertColors\": {"
//				+ "\"transform\": {"
//				+ "\"type\": \"gpii.transformer.booleanToNumber\","
//				+ "\"inputPath\": \"http://registry\\.gpii\\.net/common/invertColours\""
//				+ "} }}";
				
		String string = "[ \"applications.net\\.sourceforge\\.magnifier.id\"]";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
				.serializeNulls()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();
//		JsonPrimitive prim = new JsonPrimitive("MYITEM"+string.replace("\r\n", "")+"MYITEM");
//		JsonObject obj = new JsonObject();
//		obj.add("capabilitiesTransformations", prim);
		JsonArray capabilities = new JsonArray();
		JsonPrimitive el = new JsonPrimitive("HELPITEM"+string.replace("\r\n", ""));
		capabilities.add(el);
		JsonObject settingsInnerObj = new JsonObject();
		settingsInnerObj.add("capabilities", capabilities);
		
		String str = gson.toJson(settingsInnerObj).replace("\\\\", "\\")
				.replace("\\\"", "\"").replace("\r\n", "")
				.replace("\"MYITEM", "").replace("MYITEM\"", "")
				.replace("[\"", "").replace("\"]", "")
				.replace("\"HELPITEM[", "").replace("HELPITEM]\"", "");
		System.out.println(string);
		System.out.println(str);
		
	}

	
	public void exportSolutions(){
		
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);

			JsonArray allSolutions = new JsonArray();
			for (Solution sol : ontologyInstance.getSolutions()) {

				JsonObject solution = sol.getSolutionJsonObject();

				JsonArray clusters = sol
						.getSolutionSelectedPropertiesJsonSchema();

				if (clusters != null)
					solution.add("EASTIN Properties", clusters);

				allSolutions.add(solution);

			}

			Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
					.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
					.create();

			writeGsonAndExportFile(FILENAME_SOLUTIONS,
					gson.toJson(allSolutions));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void exportRegistryTerms() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);
			Gson gson = new Gson();
			String json = "[";
			List<RegistryTermForJSON> list = new ArrayList<RegistryTermForJSON>();

			// create a list with all terms (accepted & proposed)
			List<RegistryTerm> allTerms = new ArrayList<RegistryTerm>();
			allTerms.addAll(ontologyInstance.getRegistryTerms());
			allTerms.addAll(ontologyInstance.getProposedRegistryTerms());

			// create json for all terms
			for (int i = 0; i < allTerms.size(); i++) {
				RegistryTerm term = allTerms.get(i);
				String setname = term.getId();

				json = json
						+ "\n{\n\t\"type\":\"object\",\n\t\"properties\":{\n\t\t\"http://registry.gpii.net/common/"
						+ setname + "\":{\n\t\t\"type\":\"" + term.getType()
						+ "\",";

				json = json + "\n\t\t\"status\":\"" + term.getStatus() + "\",";

				if (term.getType().equals("boolean")) {
					json = json + "\n\t\t\"default\":" + term.getDefaultValue()
							+ "\n\t\t}\n\t}";
				} else {
					json = json + "\n\t\t\"default\":\""
							+ term.getDefaultValue() + "\",\n\t\t\"enum\":["
							+ term.getValueSpace() + "]\n\t\t}\n\t}";
				}

				json = json + "\n}";
				if (i != allTerms.size() - 1)
					json = json + ",";
			}
			json = json + "\n]";
			gson.toJson(list);

			writeGsonAndExportFile(FILENAME, json);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
