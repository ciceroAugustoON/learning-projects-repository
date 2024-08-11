package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO department(Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, obj.getName());
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				obj.setId(rs.getInt(1));
			} else {
				throw new DbException("Unexpected Error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stmt);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("UPDATE department SET name = ? WHERE Id = ?");
			stmt.setString(1, obj.getName());
			stmt.setInt(2, obj.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stmt);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stmt);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			Department dep = null;
			if (rs.next()) {
				dep = instantiateDepartment(rs);
			}
			return dep;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(stmt);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT * FROM department");
			rs = stmt.executeQuery();
			List<Department> deps = new ArrayList<>();
			while (rs.next()) {
				Department dep = new Department();
				dep.setId(rs.getInt("Id"));
				dep.setName(rs.getString("Name"));
				deps.add(dep);
			}
			return deps;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(stmt);
		}
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException{
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;
	}

}
