package de.darcade.UnityConnect.UserInterface;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.darcade.UnityConnect.BackgroundService;
import de.darcade.UnityConnect.Device;
import de.darcade.UnityConnect.UserInterface.List.DeviceItem;
import de.darcade.UnityConnect.UserInterface.List.ListAdapter;
import de.darcade.UnityConnect.UserInterface.List.SectionItem;
import de.darcade.UnityConnect_tp.R;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends ActionBarActivity {

    //
    // Action bar
    //

    private MenuItem menuProgress;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menuProgress = menu.findItem(R.id.menu_progress);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                updateComputerList();
                BackgroundService.RunCommand(MainActivity.this, new BackgroundService.InstanceCallback() {
                    @Override
                    public void onServiceStart(BackgroundService service) {
                        service.onNetworkChange();
                    }
                });
                item.setVisible(false);
                menuProgress.setVisible(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try { Thread.sleep(1500); } catch (InterruptedException e) { }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                menuProgress.setVisible(false);
                                item.setVisible(true);
                            }
                        });
                    }
                }).start();
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this,MainSettingsActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
    }



    //
    // Device list
    //

    void updateComputerList() {

        BackgroundService.RunCommand(MainActivity.this, new BackgroundService.InstanceCallback() {
            @Override
            public void onServiceStart(final BackgroundService service) {

                Collection<Device> devices = service.getDevices().values();
                final ArrayList<ListAdapter.Item> items = new ArrayList<ListAdapter.Item>();

                SectionItem section;

                Resources res = getResources();

                section = new SectionItem(res.getString(R.string.category_connected_devices));
                section.isEmpty = true;
                items.add(section);
                for(Device d : devices) {
                    if (d.isReachable() && d.isPaired()) {
                        items.add(new DeviceItem(MainActivity.this, d));
                        section.isEmpty = false;
                    }
                }

                section = new SectionItem(res.getString(R.string.category_not_paired_devices));
                section.isEmpty = true;
                items.add(section);
                for(Device d : devices) {
                    if (d.isReachable() && !d.isPaired()) {
                        items.add(new DeviceItem(MainActivity.this, d));
                        section.isEmpty = false;
                    }
                }

                section = new SectionItem(res.getString(R.string.category_remembered_devices));
                section.isEmpty = true;
                items.add(section);
                for(Device d : devices) {
                    if (!d.isReachable() && d.isPaired()) {
                        items.add(new DeviceItem(MainActivity.this, d));
                        section.isEmpty = false;
                    }
                }
                if (section.isEmpty) {
                    items.remove(items.size()-1); //Remove section
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView list = (ListView)findViewById(R.id.listView1);
                        list.setAdapter(new ListAdapter(MainActivity.this, items));
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                view.callOnClick();
                            }
                        });
                   }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        BackgroundService.RunCommand(MainActivity.this, new BackgroundService.InstanceCallback() {
            @Override
            public void onServiceStart(BackgroundService service) {
                service.onNetworkChange();
                service.setDeviceListChangedCallback(new BackgroundService.DeviceListChangedCallback() {
                    @Override
                    public void onDeviceListChanged() {
                        updateComputerList();
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        BackgroundService.RunCommand(MainActivity.this, new BackgroundService.InstanceCallback() {
            @Override
            public void onServiceStart(BackgroundService service) {
                service.setDeviceListChangedCallback(null);
            }
        });
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateComputerList();
    }

}
