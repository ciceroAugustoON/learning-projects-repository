package application;

import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		System.out.println("\n=== TEST 1: DepartmentDao.findAll() ===");
		List<Department> deps = departmentDao.findAll();
		deps.stream().forEach(System.out::println);
		System.out.println("\n=== TEST 2: DepartmentDao.findById() ===");
		Department dep = departmentDao.findById(3);
		System.out.println(dep);
		System.out.println("\n=== TEST 3: DepartmentDao.insert() ===");
		departmentDao.insert(new Department(null, "Toys"));
		deps = departmentDao.findAll();
		deps.stream().forEach(System.out::println);
		System.out.println("\n=== TEST 4: DepartmentDao.update() ===");
		dep.setName("School supplies");
		departmentDao.update(dep);
		deps = departmentDao.findAll();
		deps.stream().forEach(System.out::println);
		System.out.println("\n=== TEST 5: DepartmentDao.deleteById() ===");
		System.out.print("Select a department by Id: ");
		int id = sc.nextInt();
		departmentDao.deleteById(id);
		deps = departmentDao.findAll();
		deps.stream().forEach(System.out::println);
	}

}
