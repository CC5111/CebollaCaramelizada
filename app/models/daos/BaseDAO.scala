package models.daos

import models.persistence.SlickTables._
import models.persistence.SlickTables
import models.entities._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.CanBeQueryCondition

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import javax.inject.{Inject, Singleton}

import slick.model.Column

import scala.concurrent.Await


trait AbstractBaseDAO[T,A] {
  def insert(row : A): Future[Long]
  def insert(rows : Seq[A]): Future[Seq[Long]]
  def update(row : A): Future[Int]
  def update(rows : Seq[A]): Future[Unit]
  def findById(id : Long): Future[Option[A]]
  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def deleteById(id : Long): Future[Int]
  def deleteById(ids : Seq[Long]): Future[Int]
  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int]
}


abstract class BaseDAO[T <: BaseTable[A], A <: BaseEntity]() extends AbstractBaseDAO[T,A] with HasDatabaseConfig[JdbcProfile] {
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  protected val tableQ: TableQuery[T]

  def insert(row : A): Future[Long] ={
    insert(Seq(row)).map(_.head)
  }

  def insert(rows : Seq[A]): Future[Seq[Long]] ={
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  def update(row : A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future{0}
  }

  def update(rows : Seq[A]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  def findById(id : Long): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  def deleteById(id : Long): Future[Int] = {
    deleteById(Seq(id))
  }

  def deleteById(ids : Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

}



@Singleton
class SeriesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[SeriesTable, Series]{
  import dbConfig.driver.api._

  protected val tableQ = SlickTables.seriesTable

  def all: Future[Seq[Series]] = {
    db.run(tableQ.result)
  }
}


@Singleton
class SeasonDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[SeasonTable, Season]{
  import dbConfig.driver.api._

  protected val tableQ = SlickTables.seasonTable

  def all: Future[Seq[Season]] = {
    db.run(tableQ.result)
  }


  def seasonsOfId(id: Long) : Future[Seq[Season]]  = {
    db.run(tableQ.filter(_.seriesID === id).sortBy(_.number).result)
  }
}


@Singleton
class EpisodeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[EpisodeTable, Episode]{
  import dbConfig.driver.api._

  protected val tableQ = SlickTables.episodeTable

  def all: Future[Seq[Episode]] = {
    db.run(tableQ.result)
  }


  def episodesOfId(id: Long) : Future[Seq[Episode]]  = {
    db.run(tableQ.filter(_.seasonID === id).sortBy(_.number).result)
  }
}