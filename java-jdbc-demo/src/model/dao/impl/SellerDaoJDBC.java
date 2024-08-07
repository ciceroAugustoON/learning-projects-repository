package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT seller.*,department.Name as DepName\n"
					+ "FROM seller INNER JOIN department\n"
					+ "ON seller.DepartmentId = department.Id\n"
					+ "WHERE seller.Id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Department department = instantiateDepartment(rs);
				Seller obj = instatiateSeller(rs, department);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(stmt);
		}
	}

	private Seller instatiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(department);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepName"));
		return department;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT seller.*,department.Name as DepName\n"
					+ "FROM seller INNER JOIN department\n"
					+ "ON seller.DepartmentId = department.Id\n"
					+ "ORDER BY Name");
			rs = stmt.executeQuery();
			List<Seller> sellers = new ArrayList<>();
			Map<Integer,Department> depInstances = new HashMap<>();
			while (rs.next()) {
				Department dep = depInstances.get(rs.getInt("DepartmentId"));
				if (dep == null) {
					dep = new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
					depInstances.put(dep.getId(), dep);
				}
				Seller obj = instatiateSeller(rs, dep);
				sellers.add(obj);
			}
			return sellers;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(stmt);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT seller.*,department.Name as DepName\n"
					+ "FROM seller INNER JOIN department\n"
					+ "ON seller.DepartmentId = department.Id\n"
					+ "WHERE DepartmentId = ?\n"
					+ "ORDER BY Name");
			stmt.setInt(1, department.getId());
			rs = stmt.executeQuery();
			List<Seller> sellers = new ArrayList<>();
			while (rs.next()) {
				if (department.getName() == null) {
					department.setName(rs.getString("DepName"));
				}
				Seller obj = instatiateSeller(rs, department);
				sellers.add(obj);
			}
			return sellers;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(stmt);
		}
	}
	
}
