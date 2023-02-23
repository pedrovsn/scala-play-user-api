package models

case class User(id: Long, email: String, name: String)

case class NewUserRequest(email: String, name: String)
