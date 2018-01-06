package com.file.sharing.core.service.impl;

import static com.file.sharing.core.objects.file.ItemActionType.CREATE_DIRECTORY;
import static com.file.sharing.core.objects.file.ItemActionType.DELETE_DIRECTORY;
import static com.file.sharing.core.objects.file.ItemActionType.DELETE_FILE;
import static com.file.sharing.core.objects.file.ItemActionType.MOVE_DIRECTORY;
import static com.file.sharing.core.objects.file.ItemActionType.MOVE_FILE;
import static com.file.sharing.core.objects.file.ItemActionType.RENAME_DIRECTORY;
import static com.file.sharing.core.objects.file.ItemActionType.RENAME_FILE;
import static com.file.sharing.core.objects.file.ItemActionType.UPLOAD_FILE;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.file.sharing.core.actions.ItemAction;
import com.file.sharing.core.actions.directory.CreateDirectoryAction;
import com.file.sharing.core.actions.directory.DeleteDirectoryAction;
import com.file.sharing.core.actions.directory.MoveDirectoryAction;
import com.file.sharing.core.actions.directory.RenameDirectoryAction;
import com.file.sharing.core.actions.file.DeleteFileAction;
import com.file.sharing.core.actions.file.MoveFileAction;
import com.file.sharing.core.actions.file.RenameFileAction;
import com.file.sharing.core.actions.file.UploadFileAction;
import com.file.sharing.core.business.ItemsActionBusiness;
import com.file.sharing.core.entity.DirectoryItem;
import com.file.sharing.core.entity.FileItem;
import com.file.sharing.core.jms.ItemActionJmsSender;
import com.file.sharing.core.objects.file.ItemActionType;
import com.file.sharing.core.service.ItemActionEventSynchronization;
import com.file.sharing.core.service.ItemActionEventService;

/**
 * @author Alexandru Mihai
 * @created Nov 18, 2017
 */
@Service
@Transactional(readOnly = false)
public class ItemActionEventServiceImpl implements ItemActionEventService {

	private final ItemsActionBusiness itemsActionBusiness;

	private final ItemActionJmsSender itemActionJmsSender;

	@Autowired
	public ItemActionEventServiceImpl(ItemsActionBusiness itemsActionBusiness,
			ItemActionJmsSender itemActionJmsSender) {
		this.itemsActionBusiness = itemsActionBusiness;
		this.itemActionJmsSender = itemActionJmsSender;
	}

	@Override
	@Transactional(readOnly = false)
	public void directoryCreated(CreateDirectoryAction action) {
		registerTransactionSynchronization(action, CREATE_DIRECTORY);
		DirectoryItem directoryItem = itemsActionBusiness.saveDirectoryItem(action);
		itemsActionBusiness.saveFileItemAction(directoryItem.getId(), CREATE_DIRECTORY);
	}

	@Override
	@Transactional(readOnly = false)
	public void directoryDeleted(DeleteDirectoryAction action) {
		registerTransactionSynchronization(action, DELETE_DIRECTORY);
		itemsActionBusiness.deleteItem(action.getItemId());
		itemsActionBusiness.saveFileItemAction(action.getItemId(), DELETE_DIRECTORY);
	}

	@Override
	@Transactional(readOnly = false)
	public void directoryRanamed(RenameDirectoryAction action) {
		registerTransactionSynchronization(action, RENAME_DIRECTORY);
		itemsActionBusiness.renameItem(action.getItemId(), action.getNewItemName());
		itemsActionBusiness.saveFileItemAction(action.getItemId(), RENAME_DIRECTORY);
	}

	@Override
	@Transactional(readOnly = false)
	public void directoryMoved(MoveDirectoryAction action) {
		registerTransactionSynchronization(action, MOVE_DIRECTORY);
		itemsActionBusiness.moveItem(action.getItemId(), action.getNewParentId());
		itemsActionBusiness.saveFileItemAction(action.getItemId(), MOVE_DIRECTORY);
	}

	@Override
	@Transactional(readOnly = false)
	public void fileUploaded(UploadFileAction action) {
		registerTransactionSynchronization(action, UPLOAD_FILE);
		FileItem fileItem = itemsActionBusiness.saveFileItem(action);
		itemsActionBusiness.saveFileItemAction(fileItem.getId(), UPLOAD_FILE);
	}

	@Override
	@Transactional(readOnly = false)
	public void fileDeleted(DeleteFileAction action) {
		registerTransactionSynchronization(action, DELETE_FILE);
		itemsActionBusiness.deleteItem(action.getItemId());
		itemsActionBusiness.saveFileItemAction(action.getItemId(), DELETE_FILE);
	}

	@Override
	@Transactional(readOnly = false)
	public void fileRenamed(RenameFileAction action) {
		registerTransactionSynchronization(action, RENAME_FILE);
		itemsActionBusiness.renameItem(action.getItemId(), action.getNewItemName());
		itemsActionBusiness.saveFileItemAction(action.getItemId(), RENAME_FILE);
	}

	@Override
	@Transactional(readOnly = false)
	public void fileMoved(MoveFileAction action) {
		registerTransactionSynchronization(action, MOVE_FILE);
		itemsActionBusiness.moveItem(action.getItemId(), action.getNewParentId());
		itemsActionBusiness.saveFileItemAction(action.getItemId(), MOVE_FILE);
	}

	// --------------------------------------------------------------------------------------------------

	private void registerTransactionSynchronization(ItemAction action, ItemActionType itemActionType) {
		registerSynchronization(new ItemActionEventSynchronization(action.getUserId(),
				action.getItemName(), itemActionType, itemActionJmsSender));
	}

}