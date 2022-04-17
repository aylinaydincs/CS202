package org.ozyegin.cs.repository;

import java.util.*;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Repository
public class ProductRepository extends JdbcDaoSupport {

  final String findPS = "SELECT FROM product WHERE id = ?";
  final String findByBrandNamePS ="SELECT * FROM product WHERE brand_name=?";
  final String deleteAllPS = "DELETE FROM product";
  //final String createPS = "INSERT INTO product ( name, brandName, description) VALUES (?,?,?)";
  //final String updatePS = "UPDATE product SET name = ?,  description=?, brand_name=?, WHERE id=? ";
  final String deletePS = "DELETE FROM product WHERE id=?";
  final String getPS = "SELECT * FROM product WHERE id IN (:ids)";

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Product> ProductRowMapper = (resultSet, i) ->{
    Product product = new Product();
    product.setId(resultSet.getInt("id"));
    product.setName(resultSet.getString("name"));
    product.setDescription(resultSet.getString("description"));
    product.setBrandName(resultSet.getString("brand_name"));

    return product;
  };

  private final RowMapper<Integer> intRowMapper = (resultSet, i) -> resultSet.getInt(1);

  public Product find(int id) {
    return Objects.requireNonNull(getJdbcTemplate()).queryForObject("SELECT * FROM product WHERE id=?", new Object[]{id}, ProductRowMapper);
  }

  public List<Product> findMultiple(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    } else {
      Map<String, List<Integer>> params = new HashMap<>() {
        {
          this.put("ids", new ArrayList<>(ids));
        }
      };
      var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
      return template.query(getPS, params, ProductRowMapper);
    }

  }

  public List<Product> findByBrandName(String brandName) {
    return Objects.requireNonNull(getJdbcTemplate()).query(findByBrandNamePS,
            new Object[] {brandName}, ProductRowMapper);
  }

  public List<Integer> create(List<Product> products) {

    // Burda en son idleri return etmek için çözüm bul
    // Bu methodu detaylıca incele
    List<Integer> existIds = Objects.requireNonNull(getJdbcTemplate())
            .query("SELECT id FROM  product",intRowMapper);

    Objects.requireNonNull(getJdbcTemplate()).batchUpdate("INSERT INTO product(name, description, brand_name) VALUES (?,?,?)", products,
            10,(ps, product)->{
              ps.setString(1,product.getName());
              ps.setString(2,product.getDescription());
              ps.setString(3, product.getBrandName());
            }
    );
    List<Integer> latestIds = Objects.requireNonNull(getJdbcTemplate())
            .query("SELECT id FROM  product",intRowMapper);
    latestIds.removeAll(existIds);

    return latestIds;
  }

  public void update(List<Product> products) {
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate("UPDATE product SET name=?, description=?, brand_name=? WHERE id=?", products,
            10,
            (ps, product) -> {
              ps.setString(1, product.getName());
              ps.setString(2, product.getDescription());
              ps.setString(3, product.getBrandName());
              ps.setInt(4,product.getId());
            });
  }

  public void delete(List<Integer> ids) {
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(deletePS, ids,
            1,
            (ps, id) -> {
              ps.setInt(1, id);
            });
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }
}
