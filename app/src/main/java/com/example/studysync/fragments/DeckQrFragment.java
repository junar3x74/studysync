package com.example.studysync.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studysync.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

public class DeckQrFragment extends Fragment {

    private static final String ARG_DECK_ID = "deck_id";

    public static DeckQrFragment newInstance(String deckId) {
        DeckQrFragment fragment = new DeckQrFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DECK_ID, deckId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck_qr, container, false);

        ImageView qrImage = view.findViewById(R.id.qrImageView);
        TextView codeText = view.findViewById(R.id.qrCodeText);

        String deckId = getArguments() != null ? getArguments().getString(ARG_DECK_ID) : null;
        codeText.setText(deckId);

        // Generate QR code
        if (deckId != null) {
            QRCodeWriter writer = new QRCodeWriter();
            try {
                Bitmap bitmap = toBitmap(writer.encode(deckId, BarcodeFormat.QR_CODE, 500, 500));
                qrImage.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private Bitmap toBitmap(com.google.zxing.common.BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bmp;
    }
}