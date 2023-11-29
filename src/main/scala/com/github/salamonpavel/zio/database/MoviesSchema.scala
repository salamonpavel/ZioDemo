package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.Constants.SCHEMA
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.naming.implementations.SnakeCaseNaming.Implicits._

/**
 * An object representing a database schema.
 */
object MoviesSchema extends DBSchema(SCHEMA)