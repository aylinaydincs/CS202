package org.ozyegin.cs.repository;

import java.util.*;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }
  private final RowMapper<Integer> intRowMapper = (resultSet, i) -> resultSet.getInt(1);

  //timestamp e çevir createdDate

    /*Statement st1 = con1.createStatement();
    ResultSet rs = st1.executeQuery("select * from table1");
    PreparedStatement ps = null;

 while(rs.next())
    {
        ps = con2.prepareStatement("insert into table2 values(?,?)");
        ps.setInt(rs.getInt());
        ps.setString(rs.getString());
        ps.executeUpdate();
    } */
  public Integer order(String company, int product, int amount, Date createdDate) {
      List<Integer> id = Objects.requireNonNull(getJdbcTemplate())
            .query("INSERT INTO transaction (company_name, product_id, amount, order_date) VALUES (?,?,?,?) RETURNING transaction_id",
                    (ps)->{
                      ps.setString(1,company);
                      ps.setInt(2,product);
                      ps.setInt(3,amount);
                      ps.setDate(4, (java.sql.Date) createdDate); },
                    intRowMapper
            );
      Objects.requireNonNull(getJdbcTemplate())
              .update("INSERT INTO transaction_history (history_id ,company, product, amount, created_date)  " +
                      "VALUES (?,?,?,?,?)", id.get(0),company,product,amount,createdDate);

      return id.get(0);
  }

  public void delete(int transactionId) throws Exception {
    if(Objects.requireNonNull(getJdbcTemplate())
            .update("DELETE FROM transaction WHERE transaction_id=?",transactionId) != 1){
      throw new Exception ("Transaction Update is failed!");
    }else{
        Objects.requireNonNull(getJdbcTemplate())
                .update("DELETE FROM transaction WHERE transaction_id=?",transactionId);
    }
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transaction ");
  }
}
