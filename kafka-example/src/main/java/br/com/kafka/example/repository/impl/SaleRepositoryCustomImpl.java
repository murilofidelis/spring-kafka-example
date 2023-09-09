package br.com.kafka.example.repository.impl;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.repository.SaleRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class SaleRepositoryCustomImpl implements SaleRepositoryCustom {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void insertBatch(List<SaleDTO> sales) {
        Session hibernateSession = this.entityManager.unwrap(Session.class);
        String sql = "INSERT INTO sales.tab_sale (id, cod_product, description, brand, price, cod_user, create_at) VALUES(nextval('sales.sales_seq'), ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) ";
        hibernateSession.doWork(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int index = 1;
                for (SaleDTO sale : sales) {
                    preparedStatement.setString(1, sale.getCodProduct());
                    preparedStatement.setString(2, sale.getDescription());
                    preparedStatement.setString(3, sale.getBrand());
                    preparedStatement.setBigDecimal(4, sale.getPrice());
                    preparedStatement.setInt(5, sale.getCodUser());
                    preparedStatement.addBatch();
                    if (index % batchSize == 0) {
                        preparedStatement.executeBatch();
                    }
                    index++;
                }
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                log.error("An exception occurred in SampleNativeQueryRepository.bulkInsertName: ", e);
            }
        });
    }
}
