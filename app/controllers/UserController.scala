package controllers

import models.{NewUserRequest, User}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  implicit val userListJson = Json.format[User]
  implicit val newUserJson = Json.format[NewUserRequest]

  private val userList = new mutable.ListBuffer[User]()
  userList += User(1, "user-1@mail.com", "User 1")
  userList += User(2, "user-2@mail.com", "User 2")
  userList += User(3, "user-3@mail.com", "User 3")

  def getAll(): Action[AnyContent] = Action {
    if(userList.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(userList))
    }
  }

  def getById(userId: Long) = Action {
    val foundUser = userList.find(_.id == userId)
    foundUser match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def addUser() = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson
    val newUser: Option[NewUserRequest] = jsonObject.flatMap(
      Json.fromJson[NewUserRequest](_).asOpt
    )

    newUser match {
      case Some(newUser) =>
        val nextId = userList.map(_.id).max + 1
        val toBeAdded = User(nextId, newUser.email, newUser.name)
        userList += toBeAdded
        Created(Json.toJson(toBeAdded))
      case None =>
        BadRequest
    }
  }

  def updateById(userId: Long) = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson
    val fromRequest: Option[User] = jsonObject.flatMap(
      Json.fromJson[User](_).asOpt
    )

    val foundUser = userList.find(_.id == userId)
    foundUser match {
      case Some(user) =>
        val indexOf = userList.indexOf(user)
        userList.remove(indexOf)
        userList += fromRequest.get
        Ok(Json.toJson(fromRequest))
      case None =>
        BadRequest
    }
  }

  def deleteById(userId: Long) = Action {
    val foundUser = userList.find(_.id == userId)
    foundUser match {
      case Some(item) =>
        val indexOf = userList.indexOf(item)
        userList.remove(indexOf)
        NoContent
      case None => BadRequest
    }
  }

}
