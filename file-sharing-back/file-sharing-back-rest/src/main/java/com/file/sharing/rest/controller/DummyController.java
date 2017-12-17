package com.file.sharing.rest.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.file.sharing.core.objects.file.FileData;
import com.file.sharing.core.service.ItemActionService;
import com.file.sharing.core.utils.FileCategoryUtil;

/**
 * @author Alexandru Mihai
 * @created Dec 10, 2017
 * 
 *          TEST ONLY... to be removed in the near future :)
 * 
 */
@RestController
public class DummyController {

	private ItemActionService itemService;

	@Autowired
	public DummyController(ItemActionService itemService) {
		this.itemService = itemService;
	}

	@PostMapping(value = "/uploadFile")
	public void uploadFile(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "directoryId", required = false) Integer directoryId) {
		String fileName = file.getOriginalFilename();

		FileData fileData;
		try {
			fileData = new FileData.Builder().withFileName(fileName)
					.withExtension(FileCategoryUtil.getExtensionFromFileName(fileName)).withBytes(file.getBytes())
					.build();

			itemService.uploadFile(directoryId, fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PostMapping(value = "/createDirectory")
	public void createDirectory(@RequestParam(value = "directoryName", required = true) String directoryName,
			@RequestParam(value = "parentId", required = false) Integer parentId) {
		
		if (parentId == null) {
			itemService.createDirectory(directoryName);
		} else {
			itemService.createDirectory(parentId, directoryName);
		}
	}

	@DeleteMapping(value = "/deleteFile")
	public void deleteFile(@RequestParam(value = "fileId") Integer fileId) {
		itemService.deleteFile(fileId);
	}
	
	@PatchMapping(value = "/renameFile")
	public void renameFile(@RequestParam(value = "fileId") Integer fileId, @RequestParam(value = "newName") String newName) {
		itemService.renameFile(fileId, newName);
	}

	@PatchMapping(value = "/moveFile")
	public void moveFile(@RequestParam(value = "fileId") Integer fileId,
			@RequestParam(value = "newParentId") Integer newParentId) {
		itemService.moveFile(fileId, newParentId);
	}

	@GetMapping(value = "/downloadFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource downloadFile(@RequestParam(value = "fileId") Integer fileId, HttpServletResponse response) {
		File file = itemService.retrieveFile(fileId);
		response.setHeader("Content-Dispition", "attachementl; filename=\"" + file.getName() + "\"");
		return new FileSystemResource(file);
	}
}