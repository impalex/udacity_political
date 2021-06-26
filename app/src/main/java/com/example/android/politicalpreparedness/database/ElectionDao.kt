package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    @Query("SELECT * FROM election_table")
    fun getElections(): Flow<List<Election>>

    @Query("SELECT * FROM election_table WHERE id=:id")
    suspend fun getElectionById(id: Int): Election?

    @Query("SELECT * FROM election_table WHERE id=:id")
    fun getElectionByIdFlow(id: Int): Flow<Election?>

    @Delete
    suspend fun deleteElection(election: Election)

    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()

}