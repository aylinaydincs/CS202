package org.ozyegin.cs.repository;
// created by aylin aydin
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Company;
import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository extends JdbcDaoSupport {

  final String deleteAllPS = "DELETE FROM company";
  final String createPS = "INSERT INTO company (name, country, zip, street, phone) VALUES (?,?,?,?,?) ";

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }
  private final RowMapper<String> stringRowMapper = (resultSet, i) -> resultSet.getString(1);
  private final RowMapper<Integer> intRowMapper = (resultSet, i) -> resultSet.getInt(1);
  private final RowMapper<Company> CompanyRowMapper = (resultSet, i) ->{
    Company company = new Company();
    company.setName(resultSet.getString("name"));
    company.setCountry(resultSet.getString("country"));
    company.setZip(resultSet.getInt("zip"));
    company.setStreetInfo(resultSet.getString("street"));
    company.setPhoneNumber(resultSet.getString("phone"));

    company.setE_mails(Objects.requireNonNull(getJdbcTemplate()).query(
            "SELECT email FROM email WHERE name=?", new Object[] {company.getName()}, stringRowMapper));

    company.setCity(Objects.requireNonNull(getJdbcTemplate()).queryForObject(
            "SELECT city FROM zipCity WHERE zip=?", new Object[] {company.getZip()}, stringRowMapper));
    return company;
  };

  public Company find(String name) {
    return Objects.requireNonNull(getJdbcTemplate()).queryForObject("SELECT * FROM company WHERE name=?",
            new Object[]{name}, CompanyRowMapper);
  }


  public List<Company> findByCountry(String country) {
    List<Company> comp =Objects.requireNonNull(getJdbcTemplate()).query("SELECT * FROM company WHERE country=?"
            ,new Object[] {country},CompanyRowMapper);
    return comp;
  }

  //Exception için try-catch yazabilirsin Exception??
  //insert zipcity->zip city, into email-> name email(for döngülü)
  public String create(Company company) throws Exception {
    /* Objects.requireNonNull(getJdbcTemplate()).update(createPS,company.getName(), company.getCountry(), company.getZip(),company.getStreetInfo(), company.getPhoneNumber());*/
    try{
      String str = Objects.requireNonNull(getJdbcTemplate()).queryForObject("SELECT city FROM zipCity WHERE zip =?",
              new Object[] {company.getZip()},stringRowMapper);

      if( !company.getCity().equals(str)){
        throw new Exception ("We could not find the city!");
      }

    }catch(EmptyResultDataAccessException e){
      Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO zipCity (zip, city) VALUES (?,?) ",
              company.getZip(),company.getCity());
    }
    /*Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO zipCity (zip, city) VALUES (?,?) ",
            company.getZip(),company.getCity());*/
    if(Objects.requireNonNull(getJdbcTemplate()).update(createPS,
            company.getName(), company.getCountry(), company.getZip(),
            company.getStreetInfo(), company.getPhoneNumber())!= 1){
      throw new Exception("Company creation is failed!");
    }
    //email tablosuna name ve email i yazarsak??
    for(String mail : company.getE_mails()){
      Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO email (name, email) VALUES (?,?) ",
              company.getName(),mail);
    }
    return company.getName();
  }

  public String delete(String name) {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM email WHERE name=?", name);
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM company WHERE name=?", name);

    return name;
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM email");
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM zipCity");

  }
}
