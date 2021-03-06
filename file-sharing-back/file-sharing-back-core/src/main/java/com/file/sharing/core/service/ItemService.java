package com.file.sharing.core.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.file.sharing.core.objects.BasicItemInfo;
import com.file.sharing.core.objects.ItemActionInfo;
import com.file.sharing.core.objects.PageResult;
import com.file.sharing.core.objects.directory.DirectoryDetails;
import com.file.sharing.core.objects.file.BasicFileInfo;
import com.file.sharing.core.objects.file.FileDetails;
import com.file.sharing.core.search.ItemSearch;
import com.file.sharing.core.search.OrderValue;

/**
 * @author Alexandru Mihai
 * @created Nov 4, 2017
 */
public interface ItemService {

	/**
	 * @param itemId
	 * @return the full path of the item. For example, if an <b>item</b> is under
	 *         the directory <b>/dir/test/</b> and the item name is <b>testItem</b>,
	 *         the full path of the item will be <b><i>/dir/test/testItem</i></b>.
	 */
	String getItemFullPath(int itemId);

	/**
	 * @param directoryId
	 * @return
	 */
	DirectoryDetails getDirectoryDetails(int directoryId) throws IOException;

	/**
	 * @param fileId
	 * @return
	 */
	FileDetails getFileDetails(int fileId) throws IOException;
	
	/**
	 * Gets instances of <code>BasicItemInfo</code> for all the items under 
	 * a parent with the given parentId.
	 * In case of a null parentId, will return instances of <code>BasicItemInfo</code>
	 * for all items under the root directory.
	 * 
	 * @param parentId
	 * @return list of items with given parentId
	 */
	List<BasicItemInfo> getBasicItemInfoByParentId(Integer parentId);
	

	/**
	 * @param fileId
	 * @return
	 */
	File retrieveFile(Integer fileId) throws IOException;

	/**
	 * @param itemSearch
	 * @return
	 */
	PageResult<BasicFileInfo> searchFiles(ItemSearch itemSearch);

	/**
	 * @param itemId
	 * @return
	 */
	List<ItemActionInfo> getItemActionsInfo(Integer itemId);

	/**
	 * @param itemId
	 * @param orderValue
	 * @return
	 */
	List<ItemActionInfo> getItemActionsInfo(Integer itemId, OrderValue orderValue);

}
