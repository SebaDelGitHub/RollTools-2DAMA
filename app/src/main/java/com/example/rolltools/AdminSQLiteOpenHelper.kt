package com.example.rolltools

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
class AdminSQLiteOpenHelper(context: Context, name: String, factory: CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL
            )
        """)


        db.execSQL("""
            CREATE TABLE IF NOT EXISTS estadisticas (
                id_usuario INTEGER NOT NULL,
                dadosLanzados INTEGER DEFAULT 0,
                monedasLanzadas INTEGER DEFAULT 0,
                FOREIGN KEY(id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """)


        db.execSQL("""
            CREATE TABLE IF NOT EXISTS partidas (
                id_partida INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                titulo TEXT NOT NULL,
                juego TEXT NOT NULL,
                descripcion TEXT,
                fecha TEXT NOT NULL,
                imagen BLOB,
                FOREIGN KEY(id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
