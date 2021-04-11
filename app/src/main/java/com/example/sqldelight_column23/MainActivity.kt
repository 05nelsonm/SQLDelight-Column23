package com.example.sqldelight_column23

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.sqldelightcolumn23.Column22
import com.example.sqldelightcolumn23.Column23
import com.example.sqldelightcolumn23.ExampleDBQueries
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class Column22Id(val value: Long)

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class Column23Id(val value: Long)

object DBSetup {

    private var driver: AndroidSqliteDriver? = null
    private var queries: ExampleDBQueries? = null

    fun getQueries(context: Context): ExampleDBQueries =
        queries ?: synchronized(this) {
            queries ?: createDB(context)
        }

    private fun createDB(context: Context): ExampleDBQueries {
        AndroidSqliteDriver(
            ExampleDB.Schema,
            context.applicationContext,
            "Example.db",
        ).let { androidDriver ->
            return ExampleDB(
                androidDriver,
                Column22.Adapter(
                    object : ColumnAdapter<Column22Id, Long> {
                        override fun decode(databaseValue: Long): Column22Id {
                            return Column22Id(databaseValue)
                        }

                        override fun encode(value: Column22Id): Long {
                            return value.value
                        }
                    }
                ),
                Column23.Adapter(
                    object : ColumnAdapter<Column23Id, Long> {
                        override fun decode(databaseValue: Long): Column23Id {
                            return Column23Id(databaseValue)
                        }

                        override fun encode(value: Column23Id): Long {
                            return value.value
                        }
                    }
                )
            )
            .exampleDBQueries
            .also {
                driver = androidDriver
                queries = it
            }
        }
    }
}

/**
 * Looking at Logcat filtered by "MainActivity" will show that querying the column22 table
 * works fine, whilst querying the column23 table while it is populated throws a
 * ClassCastException.
 *
 * Insertion into the column23 table still functions, but querying it for anything does not.
 * */
class MainActivity : AppCompatActivity() {

    private val queries: ExampleDBQueries by lazy {
        DBSetup.getQueries(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Table with 22 columns utilizing an inline class for the ID does not throw
         * any exceptions.
         * */
        lifecycleScope.launchWhenStarted {
            queries.column22GetAll().asFlow().mapToList().collect { column22List ->
                for (item in column22List) {
                    Log.d(this@MainActivity.javaClass.simpleName, item.id.toString())
                }
            }
        }

        /**
         * Table with 23 columns utilizing an inline class for the ID **does** throw
         * an exception
         * */
        lifecycleScope.launchWhenStarted {
            try {
                queries.column23GetAll().asFlow().mapToList().collect { column23List ->
                    for (item in column23List) {
                        Log.d(this@MainActivity.javaClass.simpleName, item.id.toString())
                    }
                }
            } catch (e: ClassCastException) {
                Log.e(this@MainActivity.javaClass.simpleName, e.message, e)
                Toast.makeText(this@MainActivity, e.message ?: "ClassCastException Column23", Toast.LENGTH_LONG).show()
            }
        }

        // Populate tables if needed
        lifecycleScope.launch {
            populateColumn22Table()
            populateColumn23Table()
        }
    }

    private val ids = listOf<Long>(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    )

    private suspend fun populateColumn22Table() {
        Log.d(this.javaClass.simpleName, "Upserting Column22 ids ${ids.first()} - ${ids.last()}")
        withContext(Dispatchers.IO) {
            queries.transaction {

                for (id in ids) {
                    queries.column22Upsert(
                        "field1",
                        if (id % 2L == 0L) null else "field2",
                        if (id % 2L == 0L) null else "field3",
                        if (id % 2L == 0L) null else 4L,
                        if (id % 2L == 0L) null else "field5",
                        if (id % 2L == 0L) null else "field6",
                        7L,
                        "field8",
                        "field9",
                        "field10",
                        "field11",
                        12L,
                        "field13",
                        if (id % 2L == 0L) null else "field14",
                        if (id % 2L == 0L) null else 15L,
                        if (id % 2L == 0L) null else "field16",
                        if (id % 2L == 0L) null else "field17",
                        if (id % 2L == 0L) null else "field18",
                        "field19",
                        "field20",
                        "field21",
                        Column22Id(id)
                    )
                }

            }
        }
    }

    private suspend fun populateColumn23Table() {
        Log.d(this.javaClass.simpleName, "Upserting Column23 ids ${ids.first()} - ${ids.last()}")
        withContext(Dispatchers.IO) {
            queries.transaction {

                for (id in ids) {
                    queries.column23Upsert(
                        "field1",
                        if (id % 2L == 0L) null else "field2",
                        if (id % 2L == 0L) null else "field3",
                        if (id % 2L == 0L) null else 4L,
                        if (id % 2L == 0L) null else "field5",
                        if (id % 2L == 0L) null else "field6",
                        7L,
                        "field8",
                        "field9",
                        "field10",
                        "field11",
                        12L,
                        "field13",
                        if (id % 2L == 0L) null else "field14",
                        if (id % 2L == 0L) null else 15L,
                        if (id % 2L == 0L) null else "field16",
                        if (id % 2L == 0L) null else "field17",
                        if (id % 2L == 0L) null else "field18",
                        "field19",
                        "field20",
                        if (id % 2L == 0L) null else "field21",
                        22L,
                        Column23Id(id)
                    )
                }

            }
        }
    }
}