// export {schema, MarkdownParser, defaultMarkdownSerializer} from "prosemirror-markdown"

import {schema, MarkdownSerializer, MarkdownParser, defaultMarkdownParser, defaultMarkdownSerializer} from "prosemirror-markdown"
import {Schema} from "prosemirror-model"


module.exports = {
    MarkdownParser: MarkdownParser,
    defaultMarkdownParser: defaultMarkdownParser,
    defaultMarkdownSerializer: defaultMarkdownSerializer,
    MarkdownSerializer: MarkdownSerializer,
    schema: schema
};