package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.User
import cz.davidkurzica.model.UserDTO
import cz.davidkurzica.model.Users
import org.jetbrains.exposed.sql.*

class UserService {

    suspend fun get(id: Int) = dbQuery {
        Users.select { Users.id eq id }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    suspend fun insert(user: UserDTO): User {
        var key = 0
        dbQuery {
            key = Users.insert {
                it[age] = user.age
                it[firstname] = user.firstName
                it[lastname] = user.lastName
            } get Users.id
        }
        return get(key)!!
    }

    suspend fun update(user: User): User? {
        val id = user.id
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[age] = user.age
                it[firstname] = user.firstName
                it[lastname] = user.lastName
            }
        }
        return get(id)
    }

    suspend fun delete(id: Int) = dbQuery { Users.deleteWhere { Users.id eq id } > 0 }

    private fun toUser(row: ResultRow) =
        User(
            id = row[Users.id],
            firstName = row[Users.firstname],
            lastName = row[Users.lastname],
            age = row[Users.age]
        )
}