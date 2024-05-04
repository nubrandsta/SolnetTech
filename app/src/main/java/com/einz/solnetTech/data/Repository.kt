package com.einz.solnetTech.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.einz.solnetTech.data.model.Laporan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.einz.solnetTech.data.model.Teknisi
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Repository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    val userLiveData: MutableLiveData<Result<FirebaseUser?>> = MutableLiveData()
    val teknisiLiveData: MutableLiveData<Result<Teknisi?>> = MutableLiveData()

    val laporanListLiveData: MutableLiveData<Result<List<Laporan?>>> = MutableLiveData()
    val laporanLiveData: MutableLiveData<Result<Laporan?>> = MutableLiveData()
    val takeLaporanLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val activeLaporanIdLiveData: MutableLiveData<Result<String?>> = MutableLiveData()
    val updateLaporanLiveData = MutableLiveData<Result<Boolean?>>()

    val registerSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loginSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loggedOutLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()

    val changePasswordLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val changePhoneLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()



    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(Result.Success(firebaseAuth.currentUser))
        }
    }

    suspend fun register(teknisi: Teknisi, password: String) {
        userLiveData.postValue(Result.Loading)

        try {
            firebaseAuth.createUserWithEmailAndPassword(teknisi.email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        val databaseReference = FirebaseDatabase.getInstance().getReference("techs")

                        userId?.let { uid ->
                            databaseReference.child(uid).setValue(teknisi)
                                .addOnSuccessListener {
                                    registerSuccessLiveData.postValue(Result.Success(true))
                                }
                                .addOnFailureListener { exception ->
                                    registerSuccessLiveData.postValue(Result.Error(exception.message ?: "Registration failed"))
                                }
                        } ?: run {
                            registerSuccessLiveData.postValue(Result.Error("Error"))
                        }
                    } else {
                        registerSuccessLiveData.postValue(Result.Error(task.exception?.message ?: "Registration failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun login(email: String, password: String) {
        userLiveData.postValue(Result.Loading)

        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userLiveData.postValue(Result.Success(firebaseAuth.currentUser))
                        loggedOutLiveData.postValue(Result.Error("Logged in"))
                    } else {
                        userLiveData.postValue(Result.Error(task.exception?.message ?: "Login failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun logout() {
        loggedOutLiveData.postValue(Result.Success(true))
        firebaseAuth.signOut()
    }

    suspend fun getTeknisiData() {
        teknisiLiveData.postValue(Result.Loading)
        try {
            val userEmail = firebaseAuth.currentUser?.email ?: return
            val databaseReference = FirebaseDatabase.getInstance().getReference("techs")
            databaseReference.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val customer = userSnapshot.getValue(Teknisi::class.java)
                                customer?.let {
                                    teknisiLiveData.postValue(Result.Success(it))
                                    loggedOutLiveData.postValue(Result.Error("Logged in"))
                                }
                            }
                        } else {
                            teknisiLiveData.postValue(Result.Error("Customer not found"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        teknisiLiveData.postValue(Result.Error(error.message))
                    }
                })
        } catch (e: Exception) {
            teknisiLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun listenForUserReportsWithStatusZero() {
        val reportsReference = db.getReference("reports")
        laporanListLiveData.postValue(Result.Loading)

        // Real-time listener
        reportsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allReports = mutableListOf<Laporan>()

                // Iterate through each customer's reports
                dataSnapshot.children.forEach { customerReportsSnapshot ->
                    // Iterate through each report under a customer ID
                    customerReportsSnapshot.children.forEach { reportSnapshot ->
                        val report = reportSnapshot.getValue(Laporan::class.java)
                        // Check if the report's status is '0'
                        if (report?.status == 0) {
                            report?.let { allReports.add(it) }
                        }
                    }
                }

                laporanListLiveData.postValue(Result.Success(allReports))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                laporanListLiveData.postValue(Result.Error(databaseError.message))
            }
        })
    }

    fun getLaporanById(idLaporan: String) {
        resetLaporan()
        laporanLiveData.postValue(Result.Loading)

        val reportsReference = db.getReference("reports")

        reportsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var laporanFound = false

                // Iterate through each customer's reports
                for (customerSnapshot in dataSnapshot.children) {
                    // Iterate through each report under a customer ID
                    for (reportSnapshot in customerSnapshot.children) {
                        val report = reportSnapshot.getValue(Laporan::class.java)
                        if (report?.idLaporan == idLaporan) {
                            laporanLiveData.postValue(Result.Success(report))
                            laporanFound = true
                            break
                        }
                    }
                    if (laporanFound) break
                }

                if (!laporanFound) {
                    laporanLiveData.postValue(Result.Error("Laporan not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                laporanLiveData.postValue(Result.Error(databaseError.message))
            }
        })
    }

    fun updateLaporanStatusAndSetActiveIdLaporan(idLaporan: String, newStatus: Int, idTeknisi: Int) {
        takeLaporanLiveData.postValue(Result.Loading)

        // Step 1: Fetch and update Teknisi
        val teknisiReference = db.getReference("techs")
        teknisiReference.orderByChild("idTeknisi").equalTo(idTeknisi.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(teknisiSnapshot: DataSnapshot) {
                if (teknisiSnapshot.exists()) {
                    for (childSnapshot in teknisiSnapshot.children) {
                        val teknisi = childSnapshot.getValue(Teknisi::class.java)
                        childSnapshot.ref.child("activeIdLaporan").setValue(idLaporan)

                        // Step 2: Iterate through reports to find and update Laporan
                        val reportsReference = db.getReference("reports")
                        reportsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(reportsSnapshot: DataSnapshot) {
                                var laporanFound = false

                                reportsLoop@ for (customerSnapshot in reportsSnapshot.children) {
                                    for (reportSnapshot in customerSnapshot.children) {
                                        val laporan = reportSnapshot.getValue(Laporan::class.java)
                                        if (laporan?.idLaporan == idLaporan) {
                                            reportSnapshot.ref.child("status").setValue(newStatus)
                                            reportSnapshot.ref.child("idTeknisi").setValue(idTeknisi)
                                            reportSnapshot.ref.child("teknisi").setValue(teknisi?.namaTeknisi ?: "")
                                            reportSnapshot.ref.child("teknisiPhone").setValue(teknisi?.noTelpTeknisi ?: "")
                                            laporanFound = true
                                            takeLaporanLiveData.postValue(Result.Success(true))
                                            break@reportsLoop
                                        }
                                    }
                                }

                                if (!laporanFound) {
                                    takeLaporanLiveData.postValue(Result.Error("Laporan not found"))
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                takeLaporanLiveData.postValue(Result.Error(databaseError.message))
                            }
                        })
                    }
                } else {
                    takeLaporanLiveData.postValue(Result.Error("Teknisi not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                takeLaporanLiveData.postValue(Result.Error(databaseError.message))
            }
        })
    }

    fun updateLaporanStatus(idLaporan: String, newStatus: Int) {
        val reportsReference = db.getReference("reports")

        // Listen for a single event to fetch the data once
        reportsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var laporanFound = false

                // Iterate through each customer's reports
                for (customerSnapshot in dataSnapshot.children) {
                    // Iterate through each report under a customer ID
                    for (reportSnapshot in customerSnapshot.children) {
                        val report = reportSnapshot.getValue(Laporan::class.java)
                        if (report?.idLaporan == idLaporan) {
                            // Update the status of the found report
                            reportSnapshot.ref.child("status").setValue(newStatus)
                            laporanFound = true
                            break // Break out of the inner loop
                        }
                    }
                    if (laporanFound) break // Break out of the outer loop if the report is found and updated
                }

                if (!laporanFound) {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun checkForActiveLaporanId() {
        val currentUserEmail = firebaseAuth.currentUser?.email
        if (currentUserEmail == null) {
            activeLaporanIdLiveData.postValue(Result.Error("User not logged in"))
            return
        }

        val teknisiReference = db.getReference("techs")
        teknisiReference.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val teknisi = childSnapshot.getValue(Teknisi::class.java)
                        val activeIdLaporan = teknisi?.activeIdLaporan
                        if (activeIdLaporan.isNullOrEmpty()) {
                            activeLaporanIdLiveData.postValue(Result.Error("No active Laporan ID found"))
                        } else {
                            activeLaporanIdLiveData.postValue(Result.Success(activeIdLaporan))
                        }
                        break // Assuming only one matching Teknisi will be found
                    }
                } else {
                    activeLaporanIdLiveData.postValue(Result.Error("Teknisi not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activeLaporanIdLiveData.postValue(Result.Error(databaseError.message))
            }
        })
    }

    fun resetLaporan(){
        laporanLiveData.postValue(Result.Loading)
    }

    fun changePassword(newPassword: String) {
        val user: FirebaseUser? = firebaseAuth.currentUser
        changePasswordLiveData.postValue(Result.Loading)

        user?.let {
            it.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password change successful
                        changePasswordLiveData.postValue(Result.Success(true))
                    } else {
                        // Password change failed
                        changePasswordLiveData.postValue(Result.Error(task.exception?.message ?: "Failed to update password."))
                    }
                }
        } ?: run {
            // User is not signed in or user data is not available
            changePasswordLiveData.postValue(Result.Error("No signed-in user."))
        }
    }

    fun changePhone(idTeknisi: String, newPhoneNumber: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("techs")
        changePhoneLiveData.postValue(Result.Loading)

        databaseReference.child(idTeknisi).updateChildren(mapOf("noTelpTeknisi" to newPhoneNumber))
            .addOnSuccessListener {
                // Update successful
                changePhoneLiveData.postValue(Result.Success(true))
            }
            .addOnFailureListener { exception ->
                // Update failed
                changePhoneLiveData.postValue(Result.Error(exception.message ?: "Update phone number failed"))
            }
    }





}