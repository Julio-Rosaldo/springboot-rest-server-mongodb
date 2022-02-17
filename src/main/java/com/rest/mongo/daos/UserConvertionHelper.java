package com.rest.mongo.daos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rest.mongo.entities.Pagination;
import com.rest.mongo.entities.References;

public class UserConvertionHelper {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserConvertionHelper.class);

	private UserConvertionHelper() {
	};

	public static Pagination createPagination(Long page, Long pageSize, Long totalElements) {
		Double totalPagesAux = Math.ceil(totalElements.doubleValue() / pageSize.doubleValue());
		Long totalPages = totalPagesAux.longValue();

		Long lastPage = page.equals(totalPages - 1L) ? null : (totalPages - 1L);
		Long nextPage = page.equals(totalPages - 1L) ? null : (page + 1L);
		Long previousPage = page.equals(0L) ? null : (page - 1L);

		References references = new References();
		references.setLastPage(lastPage);
		references.setNextPage(nextPage);
		references.setPreviousPage(previousPage);

		Pagination pagination = new Pagination();
		pagination.setReferences(references);
		pagination.setPage(page);
		pagination.setTotalPages(totalPages);
		pagination.setTotalElements(totalElements);
		pagination.setPageSize(pageSize);

		return pagination;
	}

}
