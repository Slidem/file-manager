package com.file.sharing.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.file.sharing.core.actions.directory.CreateDirectoryAction;
import com.file.sharing.core.handler.action.ItemActionHandlerRegistry;
import com.file.sharing.core.handler.action.impl.CreateDirectoryActionHandler;
import com.file.sharing.core.objects.Context;
import com.file.sharing.core.objects.StorageInfo;
import com.file.sharing.core.service.ItemService;
import com.file.sharing.core.service.StorageService;

/**
 * @author Alexandru Mihai
 * @created Nov 12, 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemActionServiceTest {

	private static final int DUMMY_PARENT_ID = 1;

	private static final int DUMMY_USER_ID = 1;
	
	private static final String DUMMY_PATH = "/root/path";

	@Mock
	private ItemActionHandlerRegistry eventHandlerRegistry;

	@Mock
	private StorageService storageService;

	@Mock
	private ItemService itemDetailsService;

	@Mock
	private Context context;

	@InjectMocks
	private ItemActionServiceImpl unit;
	
	@Mock
	private StorageInfo storageInfo;

	@Mock
	private CreateDirectoryActionHandler handler;

	@Before
	public void prepare() {
		Mockito.when(eventHandlerRegistry.getHandler(CreateDirectoryAction.class)).thenReturn(handler);
		Mockito.when(context.getGetUserId()).thenReturn(DUMMY_USER_ID);
		Mockito.when(context.getUserStorageInfo()).thenReturn(storageInfo);
		Mockito.when(storageInfo.getLocation()).thenReturn("DUMMY_PATH");
		Mockito.when(itemDetailsService.getItemFullPath(DUMMY_PARENT_ID)).thenReturn(DUMMY_PATH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateDirectoryThrowsExceptionWhenDirectoryNameIsEmpty() {
		unit.createDirectory(null);
	}

	@Test
	public void testCreateDirectoryCreatesUnderRootWhenParentIdIsNull() {

		unit.createDirectory(null, "dummy");

		Mockito.verify(context).getUserStorageInfo();
		
		Mockito.verify(storageInfo).getLocation();

		Mockito.verify(itemDetailsService, Mockito.never()).getItemFullPath(Mockito.anyInt());

		Mockito.verify(eventHandlerRegistry).getHandler(CreateDirectoryAction.class);

	}

	@Test
	public void testCreateDirectoryCreatesUnderParentDirWhenParentIdNotNull() {

		unit.createDirectory(DUMMY_PARENT_ID, "dummy");

		Mockito.verify(context, Mockito.never()).getUserStorageInfo();
		
		Mockito.verify(storageInfo, Mockito.never()).getLocation();

		Mockito.verify(itemDetailsService).getItemFullPath(DUMMY_PARENT_ID);

		Mockito.verify(eventHandlerRegistry).getHandler(CreateDirectoryAction.class);

	}

}
