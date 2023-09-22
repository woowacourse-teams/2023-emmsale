package com.emmsale.helper;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data-test.sql")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public abstract class JpaRepositorySliceTestHelper {

}
