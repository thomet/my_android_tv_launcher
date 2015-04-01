package me.metzmacher.mylauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AppsListActivity extends Activity {

    /** Manager. */
    private PackageManager manager;
    /** Store list of apps. */
    private List<AppInfo> apps;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        loadApps();
        loadListView();
        addClickListener();
    }

    private void addClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(position).name.toString());
                AppsListActivity.this.startActivity(i);
            }
        });
    }

    private void loadListView() {
        list = (ListView) findViewById(R.id.apps_list);

        ArrayAdapter<AppInfo> adapter = new ArrayAdapter<AppInfo>(this, R.layout.list_item, apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // use view holder pattern to better performance with list view
                ViewHolderItem viewHolder = null;

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    viewHolder = new ViewHolderItem();
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                    viewHolder.label = (TextView) convertView.findViewById(R.id.label);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.name);

                    // store holder with view
                    convertView.setTag(viewHolder);
                } else {
                    // get saved holder
                    viewHolder = (ViewHolderItem) convertView.getTag();
                }

                AppInfo appInfo = apps.get(position);

                // display app info
                if (appInfo != null) {
                    viewHolder.icon.setImageDrawable(appInfo.icon);
                    viewHolder.label.setText(appInfo.label);
                    viewHolder.name.setText(appInfo.name);
                }

                return convertView;
            }

            final class ViewHolderItem {
                ImageView icon;
                TextView label;
                TextView name;
            }
        };

        list.setAdapter(adapter);
    }

    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<AppInfo>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        // load apps
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {
            AppInfo appInfo = new AppInfo();
            appInfo.label = ri.loadLabel(manager);
            appInfo.name = ri.activityInfo.packageName;
            appInfo.icon = ri.activityInfo.loadIcon(manager);
            apps.add(appInfo);
        }
    }

}
