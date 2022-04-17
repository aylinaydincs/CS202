package org.ozyegin.cs.repository;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionHistoryRepository extends JdbcDaoSupport {
  private final RowMapper<Pair> pairMapper = (resultSet, i) -> new Pair(
      resultSet.getString(1),
      resultSet.getInt(2)
  );

  private final RowMapper<String> stringMapper = (resultSet, i) -> resultSet.getString(1);

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public List<Pair> query1() {
    List<Pair> pairList = Objects.requireNonNull(getJdbcTemplate())
            .query("SELECT DISTINCT ON(history.company) history.company, history.product" +
                    "   FROM (SELECT t.company, t.product, SUM(t.amount) AS sumofamount" +
                    "               FROM transaction_history t" +
                    "               GROUP BY  t.company, t.product ) AS history" +
                    "   GROUP BY history.company, history.product, history.sumofamount " +
                    "   ORDER BY history.company, history.sumofamount DESC ",pairMapper);
    return pairList;
  }

  public List<String> query2(Date start, Date end) {
    List<String> inactiveList = Objects.requireNonNull(getJdbcTemplate())
            .query("SELECT C.name " +
            "           FROM company C " +
                    "   WHERE C.name NOT IN (SELECT T.company" +
                    "                              FROM transaction_history T " +
                    "                              WHERE T.created_date BETWEEN ? AND ? ) "
                    ,new Object[]{start,end} ,stringMapper);
    return inactiveList;
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transaction_history");
  }
}
