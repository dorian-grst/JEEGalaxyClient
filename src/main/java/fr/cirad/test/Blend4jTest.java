package fr.cirad.test;

import com.github.jmchilton.blend4j.galaxy.beans.*;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.InputSourceType;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;

import java.util.List;
import java.util.Map;

import com.github.jmchilton.blend4j.galaxy.*;

public class Blend4jTest {

	private final String galaxyUrl;
	private final String apiKey;

	public Blend4jTest(String galaxyUrl, String apiKey) {
		this.galaxyUrl = galaxyUrl;
		this.apiKey = apiKey;
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

			if (name != null) {
				System.out.println(String.format("Tool Section: %s (ID: %s)", name, id));

				for (Tool tool : tools) {
					if (tool != null) {
						String toolName = tool.getName();
						String toolId = tool.getDescription();
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
			String message = String.format("Found workflow with id '%s' and name '%s', created by '%s'", id, name,
					author);

			System.out.println(message);

			System.out.println("Workflow Inputs:");
			Map<String, WorkflowInputDefinition> inputDefinitions = workflowDetails.getInputs();

			for (Map.Entry<String, WorkflowInputDefinition> entry : inputDefinitions.entrySet()) {
				String inputIndex = entry.getKey();
				WorkflowInputDefinition inputDefinition = entry.getValue();

				String label = inputDefinition.getLabel();
				String value = inputDefinition.getValue();

				System.out.println("  Input Index: " + inputIndex);
				System.out.println("  Input Label: " + label);
				System.out.println("  Input Value: " + value);
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
		ToolExecution test;
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
						if (dataset != null) {
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

	/**
	 * Main method to execute the Galaxy API client functions. Uncomment the desired
	 * functions to run specific tasks.
	 */
	public static void main(String[] args) {
		String galaxyUrl = "https://usegalaxy.fr/";
		String apiKey = "0312f94216df267df05771f1e9906def";

		Blend4jTest galaxyApiClient = new Blend4jTest(galaxyUrl, apiKey);
		try {
			galaxyApiClient.listHistory();
//			galaxyApiClient.listTools();
//			galaxyApiClient.listWorkflows();
//			galaxyApiClient.listDatasetsInHistory();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
