package fr.cirad.test;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.HistoryUrlFeeder;
import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.InvocationBriefs;
import com.github.jmchilton.blend4j.galaxy.beans.InvocationDetails;
import com.github.jmchilton.blend4j.galaxy.beans.InvocationStepDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Job;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.InputSourceType;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowStepDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowStepDefinition.WorkflowStepOutput;
import com.sun.jersey.api.client.ClientResponse;

public class Blend4jTest {

	private final String galaxyUrl;
	private final String apiKey;

	public Blend4jTest(String galaxyUrl, String apiKey) {
		this.galaxyUrl = galaxyUrl;
		this.apiKey = apiKey;
	}

	/**
	 * Launch a job
	 */
	public void launchJob(String jobID, List<String> filesPath) throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		History history = galaxyInstance.getHistoriesClient().create(new History("Job history"));
		Job job = new Job();
		job.setToolId(jobID);
		Date date = new Date();
		job.setCreated(date);
		job.setState("Creation");

		FileLibraryUpload upload = new FileLibraryUpload();
		upload.setFile(new File(filesPath.get(0)));
		String[] file = filesPath.get(0).split("/");
		upload.setName(file[file.length - 1]);
		upload.setContent("Je ne sais pas quoi écrire pour le content");
		upload.setFileType("fasta");
		upload.setCreateType(LibraryUpload.CreateType.FILE);

		ClientResponse clientResponse = galaxyInstance.getLibrariesClient().uploadFile(history.getId(), upload);

		galaxyInstance.getJobsClient().getJobs().add(job);

		ToolsClient toolsClient = galaxyInstance.getToolsClient();
	}

	/**
	 * Lists all available tools in Galaxy along with their sections. Prints the
	 * Tool Section name, Tool name, and Tool description.
	 */
	public void listTools() throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		ToolsClient toolsClient = galaxyInstance.getToolsClient();

		List<ToolSection> toolSections = toolsClient.getTools();
		System.out.println("Available Tools:");
		for (ToolSection toolSection : toolSections) {
			String id = toolSection.getId();
			String name = toolSection.getName();
			List<Tool> tools = toolSection.getElems();

			if (name != null && "phylogenetics".equals(id)) {
				System.out.println(String.format("Tool Section: %s (ID: %s)", name, id));

				for (Tool tool : tools) {
					if (tool != null) {
						String toolName = tool.getName();
						String toolId = tool.getDescription();

//						List<ToolParameter> toolParameters = tool.getInputs();
						System.out.println(String.format("  Tool: %s (Description: %s)", toolName, toolId));
					}
				}
				System.out.println();
			}
		}
	}

	/**
	 * Lists all workflows in Galaxy along with their inputs. Prints the workflow
	 * id, name, owner, and input details.
	 */
	public void listWorkflows() throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();

		List<Workflow> workflows = workflowsClient.getWorkflows();
		for (Workflow workflow : workflows) {
			WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflow.getId());
			String author = workflowDetails.getOwner();
			String name = workflow.getName();
			String id = workflow.getId();

			System.out.println("\n" + String.format("Workflow id '%s', name '%s', created by '%s'", id, name, author));

			Map<String, WorkflowStepDefinition> stepEntry = workflowDetails.getSteps();
			for (Map.Entry<String, WorkflowStepDefinition> workflowStepDefinition : stepEntry.entrySet()) {
				WorkflowStepDefinition step = workflowStepDefinition.getValue();
				System.out.println(
						" Workflow step key '" + workflowStepDefinition.getKey() + "', type: " + step.getType());
				for (Entry<String, WorkflowStepOutput> workflowStepOutputEntry : step.getInputSteps().entrySet()) {
					WorkflowStepOutput workflowStepOutput = workflowStepOutputEntry.getValue();
					System.out.println("  Step input '" + workflowStepOutputEntry.getKey() + "': "
							+ workflowStepOutput.getSourceStep() + " / " + workflowStepOutput.getStepOutput());
				}
				for (Entry<String, Object> toolInput : step.getToolInputs().entrySet()) {
					System.out.println("  Tool input '" + toolInput.getKey() + "': " + toolInput.getValue()
							+ (toolInput.getValue() != null ? " / " + toolInput.getValue().getClass() : ""));
				}
			}

			System.out.println(" Workflow Inputs:");
			Map<String, WorkflowInputDefinition> inputDefinitions = workflowDetails.getInputs();

			for (Map.Entry<String, WorkflowInputDefinition> entry : inputDefinitions.entrySet()) {
				String inputIndex = entry.getKey();
				WorkflowInputDefinition inputDefinition = entry.getValue();

				String label = inputDefinition.getLabel();
				String value = inputDefinition.getValue();
				String uuid = inputDefinition.getUuid();

				System.out.println("  Input Index: " + inputIndex);
				System.out.println("  Input Label: " + label);
				System.out.println("  Input Value: " + value);
				System.out.println("  Input uuid: " + uuid);
				System.out.println();
			}
		}
	}

	/**
	 * Lists all histories in Galaxy. Prints the history name and id.
	 */
	public void listHistory() throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		for (History history : historiesClient.getHistories()) {
			String name = history.getName();
			String id = history.getId();
			String message = String.format("Found history with name '%s' and id '%s'", name, id);
			System.out.println(message);
		}
	}

	/**
	 * Lists all datasets in each history in Galaxy. Prints the dataset id, name,
	 * data type, and file size.
	 */
	public void listDatasetsInHistory(String historyId) throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();

		HistoryDetails historyDetails = historiesClient.showHistory(historyId);

		if (historyDetails != null) {
			System.out.println("List of datasets in history " + historyDetails.getName() + " (" + historyId + ")");
			List<HistoryContents> historyContentsList = historiesClient.showHistoryContents(historyId);
			for (HistoryContents historyContents : historyContentsList) {
				if ("dataset".equals(historyContents.getHistoryContentType())) {
					Dataset dataset = historiesClient.showDataset(historyId, historyContents.getId());
					if (dataset != null && !dataset.isDeleted()) {
						String datasetId = dataset.getId();
						String datasetName = dataset.getName();
						String datasetDataType = dataset.getDataType();
						Integer datasetFileSize = dataset.getFileSize();

						System.out.println("  Dataset ID: " + datasetId);
						System.out.println("  Dataset Name: " + datasetName);
						System.out.println("  Dataset Data Type: " + datasetDataType);
						System.out.println("  Dataset File Size: " + datasetFileSize);
						System.out.println();
					}
				}
			}
		}
	}

	public void listJobs() {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey, false);
		JobsClient jobsClient = galaxyInstance.getJobsClient();

		List<Job> jobs = jobsClient.getJobs();

		for (Job job : jobs) {
			System.out.println("Job ID: " + job.getId());
			System.out.println("Job State: " + job.getState());
			System.out.println("Job Tool ID: " + job.getToolId());
			System.out.println("--------------------------");
		}
	}

	public List<String> uploadDatasetsWorkflow(String historyId, List<String> datasetsUrls) {
		GalaxyInstance instance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		HistoriesClient hc = instance.getHistoriesClient();
		List<String> datasetsIds = new ArrayList<String>();
		Exception exp = null;
		try {
			// Check if historyId exist
			History matchingHistory = findHistoryById(historyId, hc);
			if (matchingHistory == null) {
				throw new IllegalArgumentException("No history found with the id " + historyId);
			}
			for (String datasetUrl : datasetsUrls) {
				HistoryUrlFeeder huf = new HistoryUrlFeeder(instance);
				ClientResponse resp = huf.historyUrlFeedRequest(
						new HistoryUrlFeeder.UrlFileUploadRequest(matchingHistory.getId(), datasetUrl));
				// "Too many Redirects" or 4xx 5xx error
				if (resp.getStatus() >= HttpServletResponse.SC_TEMPORARY_REDIRECT + 3) {
					throw new Exception("Remote error - " + resp.toString());
				}
				final Map<String, Object> responseObjects = resp.getEntity(Map.class);
				List<Map<String, Object>> outputs = (List<Map<String, Object>>) responseObjects.get("outputs");
				for (Map<String, Object> output : outputs) {
					datasetsIds.add((String) output.get("id"));
				}
			}
		} catch (Exception e) {
			exp = e;
		} finally {
			int httpCode;
			String msg;
			if (exp == null) {
				httpCode = HttpServletResponse.SC_ACCEPTED;
				msg = "Sent to history '" + historyId + " on Galaxy instance " + this.galaxyUrl;
			} else if (exp instanceof GalaxyResponseException) {
				httpCode = HttpServletResponse.SC_FORBIDDEN;
				msg = ((GalaxyResponseException) exp).getResponseBody();
			} else {
				exp.printStackTrace();
				httpCode = exp instanceof UnknownHostException
						|| (exp.getCause() != null && exp.getCause().getClass().equals(UnknownHostException.class))
								? HttpServletResponse.SC_NOT_FOUND
								: HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
				msg = exp.getMessage();
			}
		}
		return datasetsIds;
	}

	private Workflow findWorkflowById(String workflowId, WorkflowsClient workflowClient) {
		for (Workflow w : workflowClient.getWorkflows()) {
			if (w.getId().equals(workflowId)) {
				return w;
			}
		}
		return null;
	}

	private History findHistoryById(String historyId, HistoriesClient historiesClient) {
		for (History h : historiesClient.getHistories()) {
			if (h.getId().equals(historyId)) {
				return h;
			}
		}
		return null;
	}

	public void invokeAndMonitorWorkflow(String workflowId, String historyId, List<String> historyDatasetsIds)
			throws InterruptedException {

		// Initialization
		final GalaxyInstance instance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		final WorkflowsClient wc = instance.getWorkflowsClient();
		final HistoriesClient hc = instance.getHistoriesClient();

		// Check if workflowId exist
		Workflow matchingWorkflow = findWorkflowById(workflowId, wc);
		if (matchingWorkflow == null) {
			throw new IllegalArgumentException("No workflow found with the id " + workflowId);
		}

		// Check if historyId exist
		History matchingHistory = findHistoryById(historyId, hc);
		if (matchingHistory == null) {
			throw new IllegalArgumentException("No history found with the id " + historyId);
		}

		// Stock workflow inputs informations
		WorkflowDetails workflowDetails = wc.showWorkflow(matchingWorkflow.getId());

		// Check if history datasets id exist
		for (String historyDatasetId : historyDatasetsIds) {
			boolean idExistsInHistory = false;
			List<HistoryContents> historyContentsList = hc.showHistoryContents(historyId);
			for (HistoryContents historyContents : historyContentsList) {
				if (historyDatasetId.equals(historyContents.getId())) {
					idExistsInHistory = true;
					break;
				}
			}
			if (!idExistsInHistory) {
				throw new IllegalArgumentException(
						"The dataset with ID " + historyDatasetId + " does not exist in history.");
			}
		}

//		for (int i = 0; i < historyDatasetsMaps.size(); i++) {
//			Map<String, String> historyDatasetMap = historyDatasetsMaps.get(i);
//			for (Map.Entry<String, String> entry : historyDatasetMap.entrySet()) {
//				System.out.println("HistoryDataset KEY: " + entry.getKey());
//				System.out.println("HistoryDataset VALUE: " + entry.getValue());
//			}
//
//			System.out.println("------------------------");
//		}

		// Check if there are as many datasets as there are inputs
		if (workflowDetails.getInputs().size() != historyDatasetsIds.size()) {
			throw new IllegalArgumentException("There are not as many datasets as there are inputs.");
		}

		final WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(matchingHistory.getId()));
		inputs.setWorkflowId(matchingWorkflow.getId());
		int i = 0;
		for (String workflowInputId : workflowDetails.getInputs().keySet()) {
			String historyDatasetKey = historyDatasetsIds.get(i++);
			inputs.setInput(workflowInputId, new WorkflowInput(historyDatasetKey, InputSourceType.HDA));
		}
		final WorkflowOutputs wos = wc.runWorkflow(inputs);

		// test show invocation without step details
		assert wc.showInvocation(/* faux? */matchingWorkflow.getId(), wos.getId(), false) instanceof InvocationBriefs;

		// test show invocation with step details
//		InvocationDetails invdetails = (InvocationDetails) workflowsClient.showInvocation(matchingWorkflow.getId(),
//				wos.getId(), true);
		InvocationDetails invdetails = null;
		while (invdetails == null || invdetails.getState().equals("new")) {
			Thread.sleep(2000L);
			invdetails = (InvocationDetails) wc.showInvocation(matchingWorkflow.getId(), wos.getId(), true);
		}

		// verify basic info of the invocationDetails
		assert !invdetails.getId().isEmpty();
		assert invdetails.getUpdateTime() != null;
		assert invdetails.getHistoryId().equals(historyId);
		assert invdetails.getState().equals("scheduled");
		assert invdetails.getWorkflowId() != null;

		// verify inputs in invocationDetails
		assert !invdetails.getInputs().get("0").getId().isEmpty();
		assert invdetails.getInputs().get("0").getSrc().equals("hda");

		// unlike the WorkflowOutputs returned upon workflow invocation, the outputs
		// returned from showInvocation is usually empty,
		// because the same info is populated inside each step's outputs instead

		// verify steps in invocationDetails
		assert invdetails.getSteps().size() == 3;
		InvocationStepDetails step = invdetails.getSteps().get(2);
		assert !step.getId().isEmpty();
		assert step.getUpdateTime() != null;
		assert !step.getJobId().isEmpty();
		assert step.getOrderIndex() == 2;
		assert step.getWorkflowStepLabel() == null; // this particular tool doesn't have a label in the workflow
		assert step.getState().equals("scheduled");

		// verify jobs details in invocationDetails
		assert step.getJobs().size() == 1;
		Job job = step.getJobs().get(0);
		assert !job.getId().isEmpty();
		assert !job.getToolId().isEmpty();
		assert job.getUpdated() != null;
		// The following to asserts would pass if the test is run in debug mode, but
		// would fail if run without delay.
		// This is due to the fact that when Galaxy process workflow invocation
		// requests, it returns after jobs are queued without waiting for the jobs to
		// finish.
//	        assert job.getExitCode() == 0;
//	        assert job.getState().equals("ok");
		assert job.getCreated() != null;

		// verify outputs details in invocationDetails
		assert step.getOutputs().size() == 1;
		step.getOutputs().forEach((k, v) -> {
			assert !v.getId().isEmpty();
			assert v.getSource().equals("hda");
		});
	}

	/**
	 * Main method to execute the Galaxy API client functions. Uncomment the desired
	 * functions to run specific tasks.
	 */
	public static void main(String[] args) {
		String galaxyUrl = "https://usegalaxy.eu/";
//		String apiKey = "9c48c14919ac595ec255619d1a57b030"; // Guilhem apiKey
		String apiKey = "t8GTkJ0oQtzsETGNWMz1ADO2elVmBy9"; // Dorian apiKey
//		String userId = "6fbe329a5d08d141";
//		String historyId = "0480e71a2848c343";
//		String workflowId = "38a7ddfd0d9c28a5";
//		String toolID = "toolshed.g2.bx.psu.edu/repos/iuc/rapidnj/rapidnj/2.3.2";
//		String file1 = "/home/grasset/Documents/rice_alignment.fasta";
//		String file2 = "/home/grasset/Documents/Vanilla__4153variants__126individuals.map";
		String file3 = "/home/grasset/Documents/Vanilla__4153variants__126individuals.fasta";
//		String fileUrl = "https://gigwa.southgreen.fr/gigwa/genofilt/tmpOutput/anonymousUser/6a932dee14e86655c773d668a7d2651d/Vanilla__project1__2024-01-26__4153variants__FASTA.fasta";
		List<String> fileUrls = Arrays.asList(file3);
		Blend4jTest galaxyApiClient = new Blend4jTest(galaxyUrl, apiKey);
		try {
			List<String> historyInputsIds = galaxyApiClient.uploadDatasetsWorkflow("9054898dda5a0673", fileUrls);
//			List<String> historyInputsIds = List.of("4838ba20a6d86765aae7e2b0b6075827");
			galaxyApiClient.invokeAndMonitorWorkflow("d72664f1b3fc986b", "9054898dda5a0673", historyInputsIds);
//			galaxyApiClient.listJobs();
//			galaxyApiClient.listHistory();
//			galaxyApiClient.listTools();
//			galaxyApiClient.listWorkflows();
//			galaxyApiClient.listDatasetsInHistory("9054898dda5a0673");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
