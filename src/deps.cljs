{:foreign-libs [{:file     "js/pm.pack.js"
                 :provides ["bundle.prosemirror"]}
                {:file     "js/pmMarkdown.pack.js"
                 :requires ["bundle.prosemirror" "cljsjs.markdown-it"]
                 :provides ["bundle.prosemirror-markdown"]}]}