package com.brunof3l.locus.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class Patrimonio(
  val COD: String = "",
  val DESCRICAO: String = "",
  val LOCALIZACAO: String = "",
  val ESTADO: String = "Novo",
  val MARCA: String? = null,
  val MODELO: String? = null,
  val NUMERO_SERIE: String? = null,
  val SETOR_RESPONSAVEL: String? = null,
  val OBSERVACAO: String? = null,
  val CALIBRACAO_VENCIMENTO: Long? = null,
  val imageUrl: String? = null,
)

data class User(
  val uid: String = "",
  val displayName: String? = null,
  val email: String? = null,
  val role: String = "user",
)

class FirebaseRepository(
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
  private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
  suspend fun signIn(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
  }
  suspend fun signUp(name: String, email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password).await()
    val user = auth.currentUser
    if (user != null && name.isNotBlank()) {
      val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
      user.updateProfile(profile).await()
    }
  }
  fun signOut() { auth.signOut() }

  fun items(): Flow<List<Patrimonio>> = callbackFlow {
    val listener = db.collection("patrimonio")
      .orderBy("DESCRICAO", Query.Direction.ASCENDING)
      .addSnapshotListener { snap, err ->
        if (err != null) { trySend(emptyList()); return@addSnapshotListener }
        val list = snap?.documents?.map { it.toPatrimonio() } ?: emptyList()
        trySend(list)
      }
    awaitClose { listener.remove() }
  }

  suspend fun getByCod(cod: String): Patrimonio? {
    val doc = db.collection("patrimonio").document(cod).get().await()
    return if (doc.exists()) doc.toPatrimonio() else null
  }

  suspend fun addOrUpdate(cod: String, p: Patrimonio) {
    val data = hashMapOf<String, Any?>(
      "DESCRICAO" to p.DESCRICAO,
      "LOCALIZACAO" to p.LOCALIZACAO,
      "ESTADO" to p.ESTADO,
      "MARCA" to p.MARCA,
      "MODELO" to p.MODELO,
      "NUMERO_SERIE" to p.NUMERO_SERIE,
      "SETOR_RESPONSAVEL" to p.SETOR_RESPONSAVEL,
      "OBSERVACAO" to p.OBSERVACAO,
      "CALIBRACAO_VENCIMENTO" to p.CALIBRACAO_VENCIMENTO,
      "imageUrl" to p.imageUrl,
    ).filterValues { it != null }
    db.collection("patrimonio").document(cod).set(data).await()
  }

  fun users(): Flow<List<User>> = callbackFlow {
    val listener = db.collection("users")
      .addSnapshotListener { snap, err ->
        if (err != null) { trySend(emptyList()); return@addSnapshotListener }
        val list = snap?.documents?.map { d ->
          User(
            uid = d.id,
            displayName = d.getString("displayName"),
            email = d.getString("email"),
            role = d.getString("role") ?: "user"
          )
        } ?: emptyList()
        trySend(list)
      }
    awaitClose { listener.remove() }
  }

  suspend fun updateUserRole(uid: String, role: String) {
    db.collection("users").document(uid).update(mapOf("role" to role)).await()
  }
}

private fun DocumentSnapshot.toPatrimonio(): Patrimonio {
  val cod = id
  return Patrimonio(
    COD = cod,
    DESCRICAO = this.getString("DESCRICAO") ?: "",
    LOCALIZACAO = this.getString("LOCALIZACAO") ?: "",
    ESTADO = this.getString("ESTADO") ?: "Novo",
    MARCA = this.getString("MARCA"),
    MODELO = this.getString("MODELO"),
    NUMERO_SERIE = this.getString("NUMERO_SERIE"),
    SETOR_RESPONSAVEL = this.getString("SETOR_RESPONSAVEL"),
    OBSERVACAO = this.getString("OBSERVACAO"),
    CALIBRACAO_VENCIMENTO = this.getLong("CALIBRACAO_VENCIMENTO"),
    imageUrl = this.getString("imageUrl")
  )
}