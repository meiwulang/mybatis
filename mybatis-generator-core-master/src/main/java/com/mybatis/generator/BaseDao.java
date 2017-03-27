package com.mybatis.generator;

import java.util.List;

public interface BaseDao<T, K> {
	public void save(T entity);

	public T get(K key);

	public int count(T example);

	public List<T> query(T example);

	public int update(T entity);

	public int delete(K key);

}