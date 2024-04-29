//package com.zhangke.utopia.status.ui.richtext.android;
//
//import android.graphics.drawable.Drawable;
//import android.text.Spanned;
//import android.view.View;
//import android.widget.TextView;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class TextViewUtils {
//
//
//    public static void loadCustomEmojiInTextView(TextView view){
//        CharSequence _text=view.getText();
//        if(!(_text instanceof Spanned))
//            return;
//        Spanned text=(Spanned)_text;
//        CustomEmojiSpan[] spans=text.getSpans(0, text.length(), CustomEmojiSpan.class);
//        if(spans.length==0)
//            return;
//        int emojiSize=V.dp(20);
//        Map<Emoji, List<CustomEmojiSpan>> spansByEmoji= Arrays.stream(spans).collect(Collectors.groupingBy(s->s.emoji));
//        for(Map.Entry<Emoji, List<CustomEmojiSpan>> emoji:spansByEmoji.entrySet()){
//            ViewImageLoader.load(new ViewImageLoader.Target(){
//                @Override
//                public void setImageDrawable(Drawable d){
//                    if(d==null)
//                        return;
//                    for(CustomEmojiSpan span:emoji.getValue()){
//                        span.setDrawable(d);
//                    }
//                    view.invalidate();
//                }
//
//                @Override
//                public View getView(){
//                    return view;
//                }
//            }, null, new UrlImageLoaderRequest(emoji.getKey().url, emojiSize, emojiSize), null, false, true);
//        }
//    }
//
//}
