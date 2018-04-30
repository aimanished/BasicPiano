package com.example.a16031940.basicpiano;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.util.UUID;

public class PianoActivity extends AppCompatActivity {

    Button a, b, c, d, e, f, g, h;
    Button btnStop, btnStopRecord, btnPlay, btnRecord;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQUEST_PERMISSION_CODE = 1000;

    private SoundPool soundPool;
    private int sound_a, sound_b, sound_c, sound_d, sound_e, sound_f, sound_g, sound_h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);


        a = (Button) findViewById(R.id.a);
        b = (Button) findViewById(R.id.b);
        c = (Button) findViewById(R.id.c);
        d = (Button) findViewById(R.id.d);
        e = (Button) findViewById(R.id.e);
        f = (Button) findViewById(R.id.f);
        g = (Button) findViewById(R.id.g);
        h = (Button) findViewById(R.id.h);

        btnStop = findViewById(R.id.Stop);
        btnRecord = findViewById(R.id.record);
        btnStopRecord = findViewById(R.id.stopRecord);
        btnPlay = findViewById(R.id.playrecord);

        // HERE FLOATING BUTTON

        // in Activity Context
        final ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));

        // put the position
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this).setContentView(icon).setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT).build();

        // make smaller buttons
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);

        final ImageView menuOption1 = new ImageView(this);
        final ImageView menuOption2 = new ImageView(this);
        final ImageView menuOption3 = new ImageView(this);
        final ImageView menuOption4 = new ImageView(this);

        menuOption1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));

        menuOption2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));

        menuOption3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));

        menuOption4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));


        // attach to a bigger button.
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this).addSubActionView(rLSubBuilder.setContentView(menuOption1).build()).addSubActionView(rLSubBuilder.setContentView(menuOption2).build()).addSubActionView(rLSubBuilder.setContentView(menuOption3).build()).addSubActionView(rLSubBuilder.setContentView(menuOption4).build()).attachTo(rightLowerButton).build();


        // animation when opening or closing the button

        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                icon.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                icon.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhR);
                animation.start();
            }

        });

        menuOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckPermissionFromDevice()) {

                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/"
                            + UUID.randomUUID().toString() + "_audio_record.3gp";
                    setUpMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    menuOption3.setEnabled(false);
                    menuOption4.setEnabled(false);
                    menuOption2.setEnabled(true);
                    menuOption1.setEnabled(false);
                    Toast.makeText(PianoActivity.this, "Recording", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }            }
        });

        menuOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                menuOption2.setEnabled(false);
                menuOption3.setEnabled(true);
                menuOption1.setEnabled(true);
                menuOption4.setEnabled(true);
                Toast.makeText(PianoActivity.this, "Stop Recording", Toast.LENGTH_SHORT).show();

            }
        });

        menuOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOption4.setEnabled(true);
                menuOption2.setEnabled(false);
                menuOption1.setEnabled(false);
                menuOption3.setEnabled(true);


                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();


                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(PianoActivity.this, "Playing..", Toast.LENGTH_SHORT).show();            }
        });

        menuOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOption2.setEnabled(false);
                menuOption1.setEnabled(true);
                menuOption3.setEnabled(false);
                menuOption4.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setUpMediaRecorder();
                }
                Toast.makeText(PianoActivity.this, "Stop Playing...", Toast.LENGTH_SHORT).show();

            }

        });


        // END FLOATING BUTTON



//        btnRecord.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////                if (CheckPermissionFromDevice()) {
////
////                    pathSave = Environment.getExternalStorageDirectory()
////                            .getAbsolutePath() + "/"
////                            + UUID.randomUUID().toString() + "_audio_record.3gp";
////                    setUpMediaRecorder();
////                    try {
////                        mediaRecorder.prepare();
////                        mediaRecorder.start();
////                    } catch (IOException e1) {
////                        e1.printStackTrace();
////                    }
////
////                    btnPlay.setEnabled(false);
////                    btnStop.setEnabled(false);
////                    btnStopRecord.setEnabled(true);
////                    btnRecord.setEnabled(false);
////                    Toast.makeText(PianoActivity.this, "Recording", Toast.LENGTH_SHORT).show();
////                } else {
////                    requestPermission();
////                }
////            }
////        });
////
////        btnStopRecord.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                mediaRecorder.stop();
////                btnStopRecord.setEnabled(false);
////                btnPlay.setEnabled(true);
////                btnRecord.setEnabled(true);
////                btnStop.setEnabled(true);
////            }
////        });
////
////        btnPlay.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                btnStop.setEnabled(true);
////                btnStopRecord.setEnabled(false);
////                btnRecord.setEnabled(false);
////
////
////                mediaPlayer = new MediaPlayer();
////                try {
////                    mediaPlayer.setDataSource(pathSave);
////                    mediaPlayer.prepare();
////
////
////                } catch (IOException e1) {
////                    e1.printStackTrace();
////                }
////
////                mediaPlayer.start();
////                Toast.makeText(PianoActivity.this, "Playing..", Toast.LENGTH_SHORT).show();
////            }
////        });
////
////        btnStop.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                btnStopRecord.setEnabled(false);
////                btnRecord.setEnabled(true);
////                btnStop.setEnabled(false);
////                btnPlay.setEnabled(true);
////
////                if (mediaPlayer != null) {
////                    mediaPlayer.stop();
////                    mediaPlayer.release();
////                    setUpMediaRecorder();
////                }
////            }
 //       });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        }

        sound_a = soundPool.load(this, R.raw.anote, 1);
        sound_b = soundPool.load(this, R.raw.bnote, 1);
        sound_c = soundPool.load(this, R.raw.cnote, 1);
        sound_d = soundPool.load(this, R.raw.dnote, 1);
        sound_e = soundPool.load(this, R.raw.enote, 1);
        sound_f = soundPool.load(this, R.raw.fnote, 1);
        sound_g = soundPool.load(this, R.raw.gnote, 1);
        sound_h = soundPool.load(this, R.raw.hnote, 1);

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_a, 1, 1, 0, 0, 1);
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_b, 1, 1, 0, 0, 1);

            }
        });


        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_c, 1, 1, 0, 0, 1);

            }
        });


        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_d, 1, 1, 0, 0, 1);

            }
        });


        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_e, 1, 1, 0, 0, 1);

            }
        });


        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_f, 1, 1, 0, 0, 1);

            }
        });


        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_g, 1, 1, 0, 0, 1);

            }
        });


        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_h, 1, 1, 0, 0, 1);

            }
        });
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
            break;

        }
    }

    private boolean CheckPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}
