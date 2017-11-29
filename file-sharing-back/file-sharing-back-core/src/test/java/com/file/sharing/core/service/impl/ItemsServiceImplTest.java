package com.file.sharing.core.service.impl;

import org.junit.Before;
import org.junit.Ignore;
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
import com.file.sharing.core.service.ItemDetailsService;
import com.file.sharing.core.service.StorageService;

/**
 * @author Alexandru Mihai
 * @created Nov 12, 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemsServiceImplTest {

	private static final int DUMMY_PARENT_ID = 1;

	private static final int DUMMY_USER_ID = 1;

	@Mock
	private ItemActionHandlerRegistry eventHandlerRegistry;

	@Mock
	private StorageService storageService;

	@Mock
	private ItemDetailsService itemDetailsService;

	@Mock
	private Context context;

	@InjectMocks
	private ItemsServiceImpl unit;

	@Mock
	private CreateDirectoryActionHandler handler;

	@Before
	public void preapre() {
		Mockito.when(eventHandlerRegistry.getHandler(CreateDirectoryAction.class)).thenReturn(handler);
		Mockito.when(context.getGetUserId()).thenReturn(DUMMY_USER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateDirectoryThrowsExceptionWhenDirectoryNameIsEmpty() {
		unit.createDirectory(null);
	}

	@Ignore
	@Test
	public void testCreateDirectoryCreatesUnderRootWhenParentIdIsNull() {

		unit.createDirectory(null, "dummy");

		Mockito.verify(storageService).getStoragePath(DUMMY_USER_ID);

		Mockito.verify(itemDetailsService, Mockito.never()).getItemFullPath(Mockito.anyInt());

		Mockito.verify(eventHandlerRegistry).getHandler(CreateDirectoryAction.class);

	}

	@Ignore
	@Test
	public void testCreateDirectoryCreatesUnderParentDirWhenParentIdNotNull() {

		unit.createDirectory(DUMMY_PARENT_ID, "dummy");

		Mockito.verify(storageService, Mockito.never()).getStoragePath(DUMMY_USER_ID);

		Mockito.verify(itemDetailsService).getItemFullPath(DUMMY_PARENT_ID);

		Mockito.verify(eventHandlerRegistry).getHandler(CreateDirectoryAction.class);

	}

}
