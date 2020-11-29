package qyoga.db

import io.ebean.Database
import io.ebean.DatabaseFactory
import io.ebean.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import qyoga.QEnv


class DbModule(private val env: QEnv) {

    val dataSource = PGSimpleDataSource().apply {
        setURL(env["qyoga.db.url"])
        user = env["qyoga.db.username"]
        password = env["qyoga.db.password"]
    }

    val ebeanDb: Database by lazy {
        val serverConfig = DatabaseConfig().apply {
            isDefaultServer = true
            dataSource = this@DbModule.dataSource
            isDisableLazyLoading = true
            this.addPackage("qyoga.exercises")
        }
        DatabaseFactory.create(serverConfig)
    }

    private val flyway = Flyway().apply {
        this.dataSource = this@DbModule.dataSource
    }


    init {
        flyway.migrate()
    }

}