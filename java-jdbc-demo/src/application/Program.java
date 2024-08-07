package application;

import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		SellerDao sellerDao = DaoFactory.createSellerDao();
		System.out.println("=== TEST 1: SellerDao.findById() ===");
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);
		System.out.println("\n=== TEST 2: SellerDao.findByDepartment() ===");
		List<Seller> sellers = sellerDao.findByDepartment(new Department(2, null));
		sellers.stream().forEach(System.out::println);
		System.out.println("\n=== TEST 3: SellerDao.findAll() ===");
		sellers = sellerDao.findAll();
		sellers.stream().forEach(System.out::println);
		System.out.println("\n=== TEST 4: SellerDao.insert() ===");
		sellerDao.insert(new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.00, new Department(1, null)));
		sellers = sellerDao.findAll();
		sellers.stream().forEach(System.out::println);
	}
}
