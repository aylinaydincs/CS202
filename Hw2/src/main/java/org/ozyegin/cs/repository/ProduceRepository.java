package org.ozyegin.cs.repository;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class ProduceRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Integer> intRowMapper = (resultSet, i) -> resultSet.getInt(1);

  public Integer produce(String company, int product, int capacity) {
    //hata verirse introwapper ı sona yaz
    List<Integer> id = Objects.requireNonNull(getJdbcTemplate())
            .query("INSERT INTO  produce (company_name, product_id, capacity) VALUES (?,?,?) RETURNING produce_id",
                    (ps)->{
                      ps.setString(1,company);
                      ps.setInt(2,product);
                      ps.setInt(3,capacity);

                    },
                    intRowMapper
            );
    return id.get(0);
  }

  public void delete(int produceId) throws Exception {
     /*Objects.requireNonNull(getJdbcTemplate())
            .update("DELETE FROM produce WHERE id=?",produceId);*/
    if(Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM produce WHERE produce_id = ?",produceId) != 1){
      throw new Exception ("We couldn't find production which produced id: " + produceId );
    }
    else{
        Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM produce WHERE produce_id = ?",produceId);

    }
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM produce ");
  }
}
