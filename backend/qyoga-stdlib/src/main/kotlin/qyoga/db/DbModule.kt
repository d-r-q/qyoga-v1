package qyoga.db

import io.ebean.Database
import io.ebean.DatabaseFactory
import io.ebean.Transaction
import io.ebean.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import qyoga.Failure
import qyoga.GenericSuccess
import qyoga.Outcome
import qyoga.QEnv


class DbModule(private val env: QEnv) {

    val ebeanDb: Database by lazy {
        val serverConfig = DatabaseConfig().apply {
            isDefaultServer = true
            dataSource = this@DbModule.dataSource
            isDisableLazyLoading = true
            this.addPackage("qyoga.exercises")
        }
        DatabaseFactory.create(serverConfig)
    }

    private val dataSource = PGSimpleDataSource().apply {
        setURL(env["qyoga.db.url"])
        user = env["qyoga.db.username"]
        password = env["qyoga.db.password"]
    }

    private val flyway = Flyway().apply {
        this.dataSource = this@DbModule.dataSource
    }

    init {
        flyway.migrate()
    }

    inline fun <T> transaction(crossinline body: (Transaction) -> Outcome<T, *>): Outcome<T, *> {
        return ebeanDb.beginTransaction().use {
            try {
                val res = body(it)
                if (res is GenericSuccess) {
                    it.commit()
                }
                res
            } catch (e: Exception) {
                Failure(cause = e)
            }
        }
    }

}