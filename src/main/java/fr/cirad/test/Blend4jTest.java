package fr.cirad.test;

import java.io.File;
import java.net.UnknownHostException;
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
import com.github.jmchilton.blend4j.galaxy.beans.Job;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolParameter;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
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
	public void listDatasetsInHistory() throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey);
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();

		for (History history : historiesClient.getHistories()) {
			String id = history.getId();
			HistoryDetails historyDetails = historiesClient.showHistory(id);

			if (historyDetails != null) {
				System.out.println("Datasets");
				List<HistoryContents> historyContentsList = historiesClient.showHistoryContents(id);
				for (HistoryContents historyContents : historyContentsList) {
					if ("dataset".equals(historyContents.getHistoryContentType())) {
						Dataset dataset = historiesClient.showDataset(id, historyContents.getId());
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
	}

	public void runWorkflow(String historyName, String workflowName) throws Exception {
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey, true);
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();

		// Find history
		HistoriesClient historyClient = galaxyInstance.getHistoriesClient();
		History matchingHistory = null;
		for (History history : historyClient.getHistories()) {
			if (history.getName().equals(historyName)) {
				matchingHistory = history;
				break; // Ajoutez cette ligne pour sortir de la boucle une fois que l'historique est
						// trouvé
			}
		}

		if (matchingHistory == null) {
			System.out.println("Aucun historique trouvé avec le nom : " + historyName);
			return; // Quittez la méthode si l'historique n'est pas trouvé
		}

		String input1Id = null;
		String input2Id = null;
		for (HistoryContents historyDataset : historyClient.showHistoryContents(matchingHistory.getId())) {
			if (historyDataset.getName().equals("Input1")) {
				input1Id = historyDataset.getId();
			}
			if (historyDataset.getName().equals("Input2")) {
				input2Id = historyDataset.getId();
			}
		}

		Workflow matchingWorkflow = null;
		for (Workflow workflow : workflowsClient.getWorkflows()) {
			if (workflow.getName().equals(workflowName)) {
				matchingWorkflow = workflow;
			}
		}

		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(matchingWorkflow.getId());
		String workflowInput1Id = null;
		String workflowInput2Id = null;
		for (Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
			String label = inputEntry.getValue().getLabel();
			if (label.equals("WorkflowInput1")) {
				workflowInput1Id = inputEntry.getKey();
			}
			if (label.equals("WorkflowInput2")) {
				workflowInput2Id = inputEntry.getKey();
			}
		}
		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(matchingHistory.getId()));
		inputs.setWorkflowId(matchingWorkflow.getId());
		inputs.setInput(workflowInput1Id,
				new WorkflowInputs.WorkflowInput(input1Id, WorkflowInputs.InputSourceType.HDA));
		inputs.setInput(workflowInput2Id,
				new WorkflowInputs.WorkflowInput(input2Id, WorkflowInputs.InputSourceType.HDA));
		ToolParameter tool = new ToolParameter("from_history_id", matchingHistory.getId());

		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		System.out.println("Running workflow in history " + output.getHistoryId());
		for (String outputId : output.getOutputIds()) {
			System.out.println("  Workflow writing to output id " + outputId);
		}
	}

	public void pushFilesToGalaxyHistory(List<String> fileUrls) {
		GalaxyInstance gi = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey, true);
		HistoriesClient hc = gi.getHistoriesClient();
		String targetHistName = "TestHistory1";
		History targetHist = null;

		Exception exp = null;
		try {
			for (History h : hc.getHistories())
				if (targetHistName.equals(h.getName())) {
					System.out.println("Found existing history '" + targetHistName + "' on " + this.galaxyUrl);
					targetHist = h;
					break;
				}

			if (targetHist == null) {
				targetHist = hc.create(new History(targetHistName));
				System.out.println("History '" + targetHistName + "' created on " + this.galaxyUrl);
			}

			for (String fileUrl : fileUrls) {
				HistoryUrlFeeder huf = new HistoryUrlFeeder(gi);
				ClientResponse resp = huf
						.historyUrlFeedRequest(new HistoryUrlFeeder.UrlFileUploadRequest(targetHist.getId(), fileUrl));
				if (resp.getStatus() >= HttpServletResponse.SC_TEMPORARY_REDIRECT + 3) // "Too many Redirects" or 4xx /
																						// 5xx error
					throw new Exception("Remote error - " + resp.toString());
				final Map<String, Object> responseObjects = resp.getEntity(Map.class);
				List<Map<String, Object>> outputs = (List<Map<String, Object>>) responseObjects.get("outputs");
				System.err.println(outputs);
				for (Map<String, Object> output : outputs)
					System.err.println(output.get("id"));
			}
		} catch (Exception e) {
			exp = e;
		} finally {
			int httpCode;
			String msg;
			if (exp == null) {
				httpCode = HttpServletResponse.SC_ACCEPTED;
				msg = "sent to history '" + targetHistName + "' on Galaxy instance " + this.galaxyUrl;
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

	public void runWorkflow(String historyName) {
		final GalaxyInstance instance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey, true);
		final WorkflowsClient workflowsClient = instance.getWorkflowsClient();
		final HistoriesClient historyClient = instance.getHistoriesClient();
		History matchingHistory = null;
		for (final History history : historyClient.getHistories()) {
			if (history.getName().equals(historyName)) {
				matchingHistory = history;
			}
		}
		String input1Id = null;
		String input2Id = null;
		for (final HistoryContents historyDataset : historyClient.showHistoryContents(matchingHistory.getId())) {
			if (historyDataset.getName().equals("Input1")) {
				input1Id = historyDataset.getId();
			}
			if (historyDataset.getName().equals("Input2")) {
				input2Id = historyDataset.getId();
			}
		}

		Workflow matchingWorkflow = null;
		for (Workflow workflow : workflowsClient.getWorkflows()) {
			if (workflow.getName().equals("TestWorkflow1")) {
				matchingWorkflow = workflow;
			}
		}

		final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(matchingWorkflow.getId());
		String workflowInput1Id = null;
		String workflowInput2Id = null;
		for (final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
			final String label = inputEntry.getValue().getLabel();
			if (label.equals("WorkflowInput1")) {
				workflowInput1Id = inputEntry.getKey();
			}
			if (label.equals("WorkflowInput2")) {
				workflowInput2Id = inputEntry.getKey();
			}
		}

		final WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(matchingHistory.getId()));
		inputs.setWorkflowId(matchingWorkflow.getId());
		inputs.setInput(workflowInput1Id,
				new WorkflowInputs.WorkflowInput(input1Id, WorkflowInputs.InputSourceType.HDA));
		inputs.setInput(workflowInput2Id,
				new WorkflowInputs.WorkflowInput(input2Id, WorkflowInputs.InputSourceType.HDA));
		final WorkflowOutputs output = workflowsClient.runWorkflow(inputs);
		System.out.println("Running workflow in history " + output.getHistoryId());
		for (String outputId : output.getOutputIds()) {
			System.out.println("  Workflow writing to output id " + outputId);
		}

	}

	/**
	 * Main method to execute the Galaxy API client functions. Uncomment the desired
	 * functions to run specific tasks.
	 */
	public static void main(String[] args) {
		String galaxyUrl = "https://usegalaxy.fr/";
//		String apiKey = "9c48c14919ac595ec255619d1a57b030"; // Guilhem apiKey
		String apiKey = "917878cd12fa17e82807256c6ce3cb20"; // Dorian apiKey
//		String userId = "6fbe329a5d08d141";
//		String historyId = "0480e71a2848c343";
//		String workflowId = "38a7ddfd0d9c28a5";
//		String toolID = "toolshed.g2.bx.psu.edu/repos/iuc/rapidnj/rapidnj/2.3.2";
//		String file1 = "/home/grasset/Documents/rice_alignment.fasta";
//		String file2 = "/home/grasset/Documents/Vanilla__4153variants__126individuals.map";
//		String file3 = "/home/grasset/Documents/Vanilla__4153variants__126individuals.fasta";
//		String fileUrl = "https://gigwa.southgreen.fr/gigwa/genofilt/tmpOutput/anonymousUser/6a932dee14e86655c773d668a7d2651d/Vanilla__project1__2024-01-26__4153variants__FASTA.fasta";
//		List<String> fileUrls = Arrays.asList(file1, file2, file3);
//		List<String> parameters = new ArrayList<>();
//		parameters.add(pathfasta);

		Blend4jTest galaxyApiClient = new Blend4jTest(galaxyUrl, apiKey);
		try {
//			galaxyApiClient.pushFilesToGalaxyHistory(fileUrls);
//			galaxyApiClient.listJobs();
//			galaxyApiClient.runWorkflow("TestHistory1");
//			galaxyApiClient.listHistory();
//			galaxyApiClient.runWorkflow("TestHistory1", "TestWorkflow1");
//			galaxyApiClient.listTools();
//			galaxyApiClient.listWorkflows();
//			galaxyApiClient.listDatasetsInHistory();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
