// Bailey Seymour
// DVP6 - 1903
// CardUtils.java

package com.baileyseymour.overshare.utils;

import android.support.annotation.NonNull;

import com.baileyseymour.overshare.interfaces.Constants;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.SmartField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_CARDS;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_SAVED;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_CREATED_TIMESTAMP;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_HEX_ID;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_SAVED_BY_UID;

public class CardUtils {
    public static String sharableString(Card card) {
        StringBuilder sharableStr = new StringBuilder("------------------------------\n");
        sharableStr.append(card.getTitle());
        sharableStr.append("\n------------------------------\n");

        ArrayList<Field> fields = Field.fromListOfMaps(card.getFields());
        for (Field field : fields) {
            // Add each field's title and URL
            SmartField smartField = new SmartField(field);
            sharableStr.append(field.getTitle());
            sharableStr.append(": ").append(smartField.clipboardValue());
            sharableStr.append("\n");
        }

        return sharableStr.toString();
    }

    public static void addCardToSavedCollection(String chirpHexTestId, final FirebaseFirestore db) {
        addCardToSavedCollection(chirpHexTestId, db, COLLECTION_CARDS);
    }

    private static void addCardToSavedCollection(final String chirpHexTestId, final FirebaseFirestore db,
                                                 final String collectionName) {

        final String savedByUID = FirebaseAuth.getInstance().getUid();
        if (savedByUID == null) return;

        // Query for a card matching the hex id given
        Query query = db.collection(collectionName)
                .whereEqualTo(KEY_HEX_ID, chirpHexTestId)
                .orderBy(KEY_CREATED_TIMESTAMP, Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null) {
                            // Get documents
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            if (documents.size() > 0) {
                                // Get the first matching card
                                DocumentSnapshot snapshot = documents.get(0);
                                Map<String, Object> map = snapshot.getData();
                                if (map != null) {
                                    // Add the current UID as the saving account
                                    map.put(KEY_SAVED_BY_UID, savedByUID);

                                    // Make the createdTimestamp reflect the new saved date
                                    map.put(KEY_CREATED_TIMESTAMP, FieldValue.serverTimestamp());
                                    db.collection(COLLECTION_SAVED)
                                            .document()
                                            .set(map);
                                }

                            } else if (collectionName.equals(COLLECTION_CARDS)) {
                                addCardToSavedCollection(chirpHexTestId, db, COLLECTION_SAVED);
                            }
                        }
                    }
                });
    }
}
