package com.github.jmchilton.blend4j.galaxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sun.jersey.api.client.ClientResponse;

public class HistoryUrlFeeder extends ToolsClientImpl {

	public HistoryUrlFeeder(GalaxyInstance galaxyInstance) {
		super((GalaxyInstanceImpl) galaxyInstance);
	}

	public ClientResponse historyUrlFeedRequest(final UrlFileUploadRequest request) throws IOException {
		final Map<String, String> uploadParameters = new HashMap<String, String>();
		String fileUrl = request.getFileUrl();
		if (fileUrl.contains("http")) {
			uploadParameters.put("files_0|url_paste", fileUrl);
		} else {
			String fileContent = FileUtils.readFileToString(new File(fileUrl));
			uploadParameters.put("files_0|url_paste", fileContent);
		}
		uploadParameters.put("files_0|NAME", Paths.get(request.getFileUrl()).getFileName().toString());
		uploadParameters.put("dbkey", request.getDbKey());
		uploadParameters.put("file_type", request.getFileType());
		uploadParameters.putAll(request.getExtraParameters());
		final Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("tool_id", request.getToolId());
		requestParameters.put("history_id", request.getHistoryId());
		requestParameters.put("inputs", write(uploadParameters));
		requestParameters.put("type", "upload_dataset");
		return multipartPost(getWebResource(), requestParameters, prepareUploads(Arrays.asList()));
	}

	public static class UrlFileUploadRequest {
		private final String historyId;
		private final String fileUrl;
		private String fileType = "auto";
		private String dbKey = "?";
		private String toolId = "upload1";
		private Map<String, String> extraParameters = new HashMap<String, String>();

		public Map<String, String> getExtraParameters() {
			return extraParameters;
		}

		public void setExtraParameters(final Map<String, String> extraParameters) {
			this.extraParameters = extraParameters;
		}

		public UrlFileUploadRequest(final String historyId, final String fileUrl) {
			this.historyId = historyId;
			this.fileUrl = fileUrl;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public String getDbKey() {
			return dbKey;
		}

		public void setDbKey(String dbKey) {
			this.dbKey = dbKey;
		}

		public String getToolId() {
			return toolId;
		}

		public String getHistoryId() {
			return historyId;
		}

		@JsonProperty("files_0|url_paste")
		public String getFileUrl() {
			return fileUrl;
		}

		public void setToolId(String toolId) {
			this.toolId = toolId;
		}
	}
}
