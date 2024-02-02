package fr.cirad.test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.HistoryUrlFeeder;
import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.InvocationBriefs;
import com.github.jmchilton.blend4j.galaxy.beans.InvocationDetails;
import com.github.jmchilton.blend4j.galaxy.beans.InvocationStepDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Job;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.InputSourceType;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowStepDefinition;
import com.sun.jersey.api.client.ClientResponse;

public class Blend4jTest {

	/**
	 * The base URL of the Galaxy server.
	 */
	private final String galaxyUrl;

	/**
	 * The API key for authentication with the Galaxy server.
	 */
	private final String apiKey;

	/**
	 * An instance of GalaxyInstance, representing the connection to the Galaxy server.
	 * This instance is created during the initialization of Blend4jTest and can be reused
	 * across multiple methods to avoid redundant GalaxyInstance creation.
	 */
	private final GalaxyInstance galaxyInstance;
	
	/**
     * The Constant LOG.
     */
    private static final Logger LOG = LogManager.getLogger(Blend4jTest.class);

    /**
     * Constructor for Blend4jTest that initializes the Galaxy server URL, API key, and
     * creates an instance of GalaxyInstance for the connection.
     *
     * @param galaxyUrl The base URL of the Galaxy server.
     * @param apiKey    The API key for authentication with the Galaxy server.
     */
    public Blend4jTest(String galaxyUrl, String apiKey) {
        this.galaxyUrl = galaxyUrl;
        this.apiKey = apiKey;
        // Create an instance of GalaxyInstance for the connection to the Galaxy server.
        this.galaxyInstance = GalaxyInstanceFactory.get(this.galaxyUrl, this.apiKey, false);
    }


	/**
	 * Launch a job
	 */
	/*
	 * public void launchJob(String jobID, List<String> filesPath) throws Exception
	 * { History history = this.galaxyInstance.getHistoriesClient().create(new
	 * History("Job history")); Job job = new Job(); job.setToolId(jobID); Date date
	 * = new Date(); job.setCreated(date); job.setState("Creation");
	 * 
	 * FileLibraryUpload upload = new FileLibraryUpload(); upload.setFile(new
	 * File(filesPath.get(0))); String[] file = filesPath.get(0).split("/");
	 * upload.setName(file[file.length - 1]);
	 * upload.setContent("Je ne sais pas quoi écrire pour le content");
	 * upload.setFileType("fasta");
	 * upload.setCreateType(LibraryUpload.CreateType.FILE);
	 * 
	 * ClientResponse clientResponse =
	 * galaxyInstance.getLibrariesClient().uploadFile(history.getId(), upload);
	 * 
	 * galaxyInstance.getJobsClient().getJobs().add(job);
	 * 
	 * ToolsClient toolsClient = galaxyInstance.getToolsClient(); }
	 */
    
    /**
	 * Lists all workflows in Galaxy along with their inputs. Prints the workflow
	 * id, name, owner, and input details.
	 */
	public void printWorkflows()  throws Exception {
		WorkflowsClient wc = this.galaxyInstance.getWorkflowsClient();
		System.out.println("Jobs list :");
		List<Workflow> ws = wc.getWorkflows();
		for (Workflow w : ws) {
			System.out.println("  - Workflow ID: "  + w.getId());
			System.out.println("    Workflow Name: "  + w.getName());
			System.out.println("    Workflow Owner: "  + w.getOwner());

		}
	}

	/**
	 * Lists all histories in Galaxy. Prints the history name and id.
	 */
	public void printHistories() throws Exception {
		HistoriesClient hc = this.galaxyInstance.getHistoriesClient();
		System.out.println("Histories list :");
		for (History h : hc.getHistories()) {
			System.out.println("  - History ID: "  + h.getId());
			System.out.println("    History Name: " + h.getName());
		}
	}

	/**
	 * Lists all datasets that are not deleted in each history in Galaxy. Prints the dataset id, name, data type, and file size.
	 */
	public void printDatasetsInHistory(String historyId) throws Exception {
		HistoriesClient hcl = this.galaxyInstance.getHistoriesClient();
		HistoryDetails hd = hcl.showHistory(historyId);
		System.out.println("List of datasets in history " + hd.getName() + " (id : " + historyId + ")");
		List<HistoryContents> hcList = hcl.showHistoryContents(historyId);
		for (HistoryContents hco : hcList) {
			if ("dataset".equals(hco.getHistoryContentType())) {
				Dataset dataset = hcl.showDataset(historyId, hco.getId());
				if (dataset != null && !dataset.isDeleted()) {
					System.out.println("  - Dataset ID: " + dataset.getId());
					System.out.println("    Dataset Name: " + dataset.getName());
					System.out.println("    Dataset Data Type: " + dataset.getDataTypeExt());
					System.out.println("    Dataset File Size: " + dataset.getFileSize());
				}
			}
		}
	}

	/**
	 * Lists all available tools in Galaxy along with their sections. Prints the
	 * Tool Section name, Tool name, and Tool description.
	 */
	public void printTools() throws Exception {
		ToolsClient toolsClient = this.galaxyInstance.getToolsClient();
		List<ToolSection> toolSections = toolsClient.getTools();
		System.out.println("Available Tools:");
		for (ToolSection toolSection : toolSections) {
			String id = toolSection.getId();
			String name = toolSection.getName();
			List<Tool> tools = toolSection.getElems();
			if (name != null) {
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
	 * Prints information about the jobs in the Galaxy instance.
	 */
	public void printJobs() {
        JobsClient jc = this.galaxyInstance.getJobsClient();
		List<Job> js = jc.getJobs();
		System.out.println("List of jobs");
		for (Job j : js) {
			System.out.println("  - Job ID: " + j.getId());
			System.out.println("    Job State: " + j.getState());
			System.out.println("    Job Tool ID: " + j.getToolId());
		}
	}
	
	/**
	 * Retrieves the input formats for each step in a workflow.
	 *
	 * @param workflowId    The ID of the workflow to get input formats from.
	 * @return              A Map containing stepId and corresponding list of input formats.
	 * @throws 				IllegalArgumentException if the specified workflowId is not found.
	 */
	public Map<String, List<String>> getWorkflowInputFormat(String workflowId) {	    
	    // Get the WorkflowsClient from the GalaxyInstance
	    final WorkflowsClient wc = this.galaxyInstance.getWorkflowsClient();

	    // Check if the workflowId exists
	    Workflow matchingWorkflow = findWorkflowById(workflowId, wc);
	    if (matchingWorkflow == null) {
	        throw new IllegalArgumentException("No workflow found with the id " + workflowId);
	    }

	    // Store workflow inputs information
	    WorkflowDetails wd = wc.showWorkflow(matchingWorkflow.getId());

	    // Create a Map to store stepId and toolInputValue pairs
	    Map<String, List<String>> result = new HashMap<>();

	    // Iterate through each step in the workflow
	    for (Map.Entry<String, WorkflowStepDefinition> stepEntry : wd.getSteps().entrySet()) {
	        String stepId = stepEntry.getKey();
	        WorkflowStepDefinition stepDefinition = stepEntry.getValue();

	        // Retrieve information about the step
	        Map<String, WorkflowStepDefinition.WorkflowStepOutput> inputSteps = stepDefinition.getInputSteps();
	        String stepType = stepDefinition.getType();

	        // Check if the step type is "data_input"
	        if ("data_input".equals(stepType)) {
	            Map<String, Object> stepToolInputs = stepDefinition.getToolInputs();
	            if (stepToolInputs != null) {
	                boolean foundFormatKey = false;
	                
	                // Iterate through each tool input entry
	                for (Map.Entry<String, Object> toolInputEntry : stepToolInputs.entrySet()) {
	                    String toolInputKey = toolInputEntry.getKey();
	                    
	                    // Check if the tool input key is "format"
	                    if ("format".equals(toolInputKey)) {
	                        List<String> toolInputValue = (List<String>) toolInputEntry.getValue();
	                        result.put(stepId, toolInputValue);
	                        foundFormatKey = true;
	                        break;
	                    }
	                }
	                
	                // If "format" key not found, add null to result and log a warning
	                if (!foundFormatKey) {
	                    LOG.warn("Format not found in stepId " + stepId);
	                    result.put(stepId, null);
	                }
	            }
	        }
	    }
	    return result;
	}
	
	/**
	 * Finds workflows compatible with the provided files based on input formats.
	 *
	 * @param filesNames	A Map representing file paths and their corresponding formats.
	 * @return 				A List of workflow IDs that are compatible with the given files.
	 */
	public List<String> getWorkflowCompatibleWithFiles(Map<String, String> filesNames) {
	    final WorkflowsClient wc = this.galaxyInstance.getWorkflowsClient();
	    // Initialize a list to store compatible workflow IDs
	    List<String> compatibleWorkflows = new ArrayList<String>();
	    // Iterate through each workflow
	    for (Workflow workflow : wc.getWorkflows()) {
	        // Get the ID of the current workflow
	        String workflowId = workflow.getId();
	        // Get the input formats expected by the workflow
	        Map<String, List<String>> workflowFormats = getWorkflowInputFormat(workflowId);
	        // Check if the workflow is compatible with the provided files
	        if (isWorkflowCompatible(workflowFormats, filesNames)) {
	            // Add the workflow ID to the list of compatible workflows
	            compatibleWorkflows.add(workflowId);
	        }
	    }
	    // Return the list of compatible workflow IDs
	    return compatibleWorkflows;
	}

	/**
	 * Uploads datasets to a history.
	 *
	 * @param historyId      The ID of the history where the datasets will be uploaded.
	 * @param datasetsUrls   A list of URLs representing the datasets to be uploaded.
	 * @return A list of dataset IDs in the history after the upload.
	 * TODO: launch uploads in parallel (check waitForDatasetCompletion())
	 */
	public List<String> uploadDatasetsWorkflow(String historyId, List<String> datasetsUrls) {
		HistoriesClient hc = this.galaxyInstance.getHistoriesClient();
		List<String> datasetsIds = new ArrayList<String>();
		Exception exp = null;
		try {
			History matchingHistory = findHistoryById(historyId, hc);
			if (matchingHistory == null) {
				throw new IllegalArgumentException("No history found with the id " + historyId);
			}
			for (String datasetUrl : datasetsUrls) {
				HistoryUrlFeeder huf = new HistoryUrlFeeder(this.galaxyInstance);
				ClientResponse resp = huf.historyUrlFeedRequest(new HistoryUrlFeeder.UrlFileUploadRequest(matchingHistory.getId(), datasetUrl));
				// "Too many Redirects" or 4xx 5xx error
				if (resp.getStatus() >= HttpServletResponse.SC_TEMPORARY_REDIRECT + 3) {
					throw new Exception("Remote error - " + resp.toString());
				}
				final Map<String, Object> responseObjects = resp.getEntity(Map.class);
				List<Map<String, Object>> outputs = (List<Map<String, Object>>) responseObjects.get("outputs");
				for (Map<String, Object> output : outputs) {
					String datasetId = (String) output.get("id");
					datasetsIds.add(datasetId);
					waitForDatasetCompletion(datasetId, historyId);
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
	
	/**
	 * Invokes a workflow, monitors its progress, and handles datasets in the associated history.
	 *
	 * @param workflowId           	The ID of the workflow to be invoked.
	 * @param historyId            	The ID of the history where the workflow results will be stored.
	 * @param historyDatasetsIds   	A list of dataset IDs in the history to be monitored.
	 * @throws InterruptedException	If the execution is interrupted while waiting for the workflow to complete.
	 */
	public void invokeAndMonitorWorkflow(String workflowId, String historyId, List<String> historyDatasetsIds) throws InterruptedException {
		// Initialization
		final WorkflowsClient wc = this.galaxyInstance.getWorkflowsClient();
		final HistoriesClient hc = this.galaxyInstance.getHistoriesClient();
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
	
	public String getDatasetState(String historyId, String datasetId) {
		HistoriesClient hcl = this.galaxyInstance.getHistoriesClient();
		Dataset d = hcl.showDataset(historyId, datasetId);
		return d.getState();
	}
	
	/**
	 * Waits for the completion of a dataset upload by repeatedly checking its state.
	 *
	 * @param inputId   The ID of the input dataset.
	 * @param historyId The ID of the history containing the dataset.
	 * TODO: launch uploads in parallel (check uploadDatasetsWorkflow())
	 */
	private void waitForDatasetCompletion(String inputId, String historyId) {
	    String state = "";
	    while (!"ok".equals(state)) {
	        state = getDatasetState(historyId, inputId);
	        if ("ok".equals(state)) {
	            LOG.info("State for input " + inputId + " is now: " + state + ". Uploaded !");
	        } else {
	        	LOG.info("State for input " + inputId + " is: " + state + ". Waiting 5 seconds before checking again.");
	            try {
	                Thread.sleep(5000); // Pause de 5 secondes
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	        }
	    }
	}


	/**
	 * Checks if a workflow is compatible with the provided files based on input formats.
	 *
	 * @param workflowInputsFormats		A Map representing workflow input IDs and the corresponding formats they expect.
	 * @param filesNames           		A Map representing file paths and their corresponding formats.
	 * @return 							True if the workflow is compatible, false otherwise.
	 */
	private boolean isWorkflowCompatible(Map<String, List<String>> workflowInputsFormats, Map<String, String> filesNames) {
	    // Iterate through each entry in the workflowInputsFormats Map
	    for (Map.Entry<String, List<String>> entry : workflowInputsFormats.entrySet()) {
	        // Get the expected formats for the current workflow input
	        List<String> expectedFormats = entry.getValue();
	        // Check if at least one file has a matching format for the current input
	        boolean hasMatchingFormat = filesNames.values().stream().anyMatch(expectedFormats::contains);
	        // If no matching format is found, the workflow is not compatible
	        if (!hasMatchingFormat) {
	            return false;
	        }
	    }
	    // Check if at least one file has a format compatible with any of the workflow inputs
	    return filesNames.values().stream().anyMatch(format ->
	            workflowInputsFormats.values().stream().anyMatch(list -> list.contains(format)));
	}

	/**
	 * Finds a workflow by its ID from the specified WorkflowsClient.
	 * @param workflowId       The ID of the workflow to find.
	 * @param workflowClient   The WorkflowsClient instance used to retrieve workflows.
	 * @return                 The found Workflow instance or null if not found.
	 */
	private Workflow findWorkflowById(String workflowId, WorkflowsClient workflowClient) {
	    // Iterate through each workflow in the WorkflowsClient
	    for (Workflow w : workflowClient.getWorkflows()) {
	        // Check if the current workflow's ID matches the target ID
	        if (w.getId().equals(workflowId)) {
	            return w; // Return the found workflow
	        }
	    }
	    return null; // Return null if the workflow is not found
	}

	/**
	 * Finds a history by its ID from the specified HistoriesClient.
	 *
	 * @param historyId         The ID of the history to find.
	 * @param historiesClient   The HistoriesClient instance used to retrieve histories.
	 * @return                  The found History instance or null if not found.
	 */
	private History findHistoryById(String historyId, HistoriesClient historiesClient) {
	    // Iterate through each history in the HistoriesClient
	    for (History h : historiesClient.getHistories()) {
	        // Check if the current history's ID matches the target ID
	        if (h.getId().equals(historyId)) {
	            return h; // Return the found history
	        }
	    }
	    return null; // Return null if the history is not found
	}


	/**
	 * Main method to execute the Galaxy API client functions. Uncomment the desired
	 * functions to run specific tasks.
	 */
	public static void main(String[] args) {
		String galaxyUrl = "https://usegalaxy.eu/";
//		String apiKey = "9c48c14919ac595ec255619d1a57b030"; // Guilhem apiKey
		String apiKey = "t8GTkJ0oQtzsETGNWMz1ADO2elVmBy9"; // Dorian apiKey
		Blend4jTest galaxyApiClient = new Blend4jTest(galaxyUrl, apiKey);

//		String file1 = "/home/grasset/Documents/rice_alignment.fasta";
//		String file2 = "/home/grasset/Documents/Vanilla__4153variants__126individuals.map";
//		String file3 = "/home/grasset/Documents/Vanilla__4153variants__126individuals.fasta";
//		String fileUrl = "https://gigwa.southgreen.fr/gigwa/genofilt/tmpOutput/anonymousUser/6a932dee14e86655c773d668a7d2651d/Vanilla__project1__2024-01-26__4153variants__FASTA.fasta";
//		List<String> fileUrls = Arrays.asList(file1, file3);
		
//		Map<String, String> filesNames = new HashMap<String, String>();
//		filesNames.put("/home/bonsoir", "fasta.gz");
//		filesNames.put("/home/bonsoir1", "fasta");
//		filesNames.put("/home/bonsoir2", "tsv");

		try {
//			galaxyApiClient.printWorkflows();
//			galaxyApiClient.printHistories();
//			galaxyApiClient.printDatasetsInHistory("9054898dda5a0673");
//			galaxyApiClient.printTools();
//			galaxyApiClient.printJobs();
//			System.out.println(galaxyApiClient.getWorkflowInputFormat("432bd822758f6e18"));
//			System.out.println(galaxyApiClient.getWorkflowCompatibleWithFiles(filesNames));
//			List<String> historyInputsIds = galaxyApiClient.uploadDatasetsWorkflow("9054898dda5a0673", fileUrls);
//			galaxyApiClient.invokeAndMonitorWorkflow("d72664f1b3fc986b", "9054898dda5a0673", historyInputsIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
