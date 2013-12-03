/*
 * Copyright (c) 2013 Kinvey Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kinvey.sample.contentviewr.windows;

import android.view.View;
import android.webkit.WebView;
import com.kinvey.sample.contentviewr.model.ContentItem;
import com.kinvey.sample.contentviewr.R;
import com.kinvey.sample.contentviewr.core.ContentFragment;

/**
 * @author edwardf
 */
public class InfographViewer extends ContentFragment {


    private WebView webview;

    private ContentItem content;

    public static InfographViewer newInstance(ContentItem item){
        InfographViewer frag = new InfographViewer();
        frag.content = item;
        return frag;

    }


    @Override
    public int getViewID() {
        return R.layout.fragment_infographic;
    }

    @Override
    public void bindViews(View v){
        webview = (WebView) v.findViewById(R.id.infographic_webview);
        webview.loadUrl(content.getLocation());
    }


}
