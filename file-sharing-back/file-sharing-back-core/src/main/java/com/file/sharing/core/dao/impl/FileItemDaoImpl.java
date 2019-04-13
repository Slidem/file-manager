package com.file.sharing.core.dao.impl;

import static com.file.sharing.core.search.ItemSearchOrder.BY_NAME;
import static com.file.sharing.core.search.ItemSearchOrder.BY_ULOAD_DATE;

import java.lang.reflect.Array;
import java.util.*;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.swing.text.html.Option;

import com.file.sharing.core.entity.UserStorage;
import org.springframework.stereotype.Repository;

import com.file.sharing.core.dao.AbstractDaoImpl;
import com.file.sharing.core.dao.FileItemDao;
import com.file.sharing.core.entity.FileItem;
import com.file.sharing.core.entity.FileItemCategory;
import com.file.sharing.core.objects.PageResult;
import com.file.sharing.core.objects.impl.PageResultImpl;
import com.file.sharing.core.search.ItemSearch;
import com.file.sharing.core.search.ItemSearchOrder;
import com.file.sharing.core.search.OrderValue;
import com.file.sharing.core.search.PageSearch;

/**
 * @author Alexandru Mihai
 * @created Nov 11, 2017
 */
@Repository
public class FileItemDaoImpl extends AbstractDaoImpl<FileItem> implements FileItemDao {

	private static final String CATEGORY = "category";

	@Override
	public PageResult<FileItem> getItemsBasedOnCriteria(ItemSearch itemSearch) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FileItem> query = cb.createQuery(FileItem.class);
		Root<FileItem> root = query.from(FileItem.class);
		Join<FileItem, FileItemCategory> itemCategoryJoin = root.join(CATEGORY, JoinType.LEFT);
		
		List<Predicate> criterias = getCriterias(itemSearch, cb, root, itemCategoryJoin);
		List<Order> orders = getOrders(itemSearch, cb, root);
		
		PageSearch pageSearch = itemSearch.getPageSearch();

		//@formatter:off
		TypedQuery<FileItem> typeQuery = entityManager.createQuery(
														   query
				                                          .select(root)
												          .where(getListAsArray(criterias, Predicate.class))
												          .orderBy(orders));
		//@formatter:on
		int pageSize = pageSearch.getPageSize();
		int pageNumber = pageSearch.getPageNumber();

		typeQuery.setFirstResult((pageNumber - 1) * pageSize);
		typeQuery.setMaxResults(pageNumber * pageSize + 1);

		List<FileItem> result = typeQuery.getResultList();
		if (result.isEmpty()) {
			return PageResultImpl.emptyResult();
		}
		long totalRecordCount = getMaxRowCount(cb, itemSearch);
		long totalPageCount = getTotalPageCount(totalRecordCount, itemSearch);
		return PageResultImpl.of(result, totalPageCount, pageSize);
	}


	@Override
	public Optional<Long> sumOfAllUserFiles(Integer userId) {
		final Query query = entityManager.createQuery("select SUM(f.size) from FileItem f where f.user.id=:userId");
		query.setParameter("userId", userId);
		Long result = (Long) query.getSingleResult();
		return Optional.ofNullable(result);
	}

	// --------------------------------------------------------------------------

	private long getMaxRowCount(CriteriaBuilder cb, ItemSearch itemSearch) {
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<FileItem> fileItem = cq.from(FileItem.class);
		Join<FileItem, FileItemCategory> itemCategoryJoin = fileItem.join(CATEGORY, JoinType.LEFT);
		
		List<Predicate> criterias = getCriterias(itemSearch, cb, fileItem, itemCategoryJoin);
		
		cq.select(cb.count(fileItem)).where(getListAsArray(criterias, Predicate.class));

		return entityManager.createQuery(cq).getSingleResult();
	}

	private List<Predicate> getCriterias(ItemSearch itemSearch, CriteriaBuilder cb,
			Root<FileItem> root, Join<FileItem, FileItemCategory> itemCategoryJoin) {
		List<Predicate> predicates = new ArrayList<>();

		// @formatter:off
		itemSearch.getItemName().ifPresent(itemName -> 
			predicates.add(cb.like(root.get("name"), "%" + itemName + "%"))
		);
		
		itemSearch.getExtension().ifPresent(extension -> 
			predicates.add(cb.equal(itemCategoryJoin.get("extension"), extension))
		);
		
		itemSearch.getCategories().ifPresent(categories -> 
			predicates.add(itemCategoryJoin.get(CATEGORY).in(categories))
		);
		// @formatter:on
		return predicates;
	}

	private List<Order> getOrders(ItemSearch itemSearch, CriteriaBuilder cb, Root<FileItem> root) {
		List<Order> orders = new ArrayList<>();
		Map<ItemSearchOrder, OrderValue> orderMap = itemSearch.getOrderCriteriaMap();

		if (orderMap.containsKey(BY_NAME)) {
			addOrder(cb, root, orders, orderMap, "name", BY_NAME);
		}

		if (orderMap.containsKey(BY_ULOAD_DATE)) {
			addOrder(cb, root, orders, orderMap, "uploadTime", BY_ULOAD_DATE);
		}
		return orders;
	}

	private void addOrder(CriteriaBuilder cb, Root<FileItem> root, List<Order> orders,
			Map<ItemSearchOrder, OrderValue> orderMap, String itemElementName, ItemSearchOrder searchOrder) {

		Path<Set<String>> itemNamePath = root.get(itemElementName);
		OrderValue orderValue = orderMap.get(searchOrder);
		Order order = OrderValue.ASC == orderValue ? cb.asc(itemNamePath) : cb.desc(itemNamePath);
		orders.add(order);

	}

	private <T> T[] getListAsArray(List<T> list, Class<T> type) {
		@SuppressWarnings("unchecked")
		T[] newArray = (T[]) Array.newInstance(type, list.size());

		return list.toArray(newArray);
	}

	private long getTotalPageCount(long totalRecordCount, ItemSearch itemSearch) {
		long pageSize = itemSearch.getPageSearch().getPageSize();

		long totalPageCount = totalRecordCount / pageSize;

		if (totalRecordCount % pageSize != 0) {
			totalPageCount++;
		}

		return totalPageCount;
	}

}
