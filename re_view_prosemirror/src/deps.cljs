{:foreign-libs [{:file     "js/pm.pack.js"
                 :provides ["pack.prosemirror"]}
                {:file     "js/pmMarkdown.pack.js"
                 :requires ["pack.prosemirror" "cljsjs.markdown-it"]
                 :provides ["pack.prosemirror-markdown"]}]
 :npm-deps {:prosemirror-markdown "^0.22.0"}}