{:foreign-libs [{:file     "js/pm.pack.js"
                 :provides ["pack.prosemirror"]}
                {:file     "js/pmMarkdown.pack.js"
                 :requires ["pack.prosemirror" "cljsjs.markdown-it"]
                 :provides ["pack.prosemirror-markdown"]}]}