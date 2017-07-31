// export {schema, MarkdownParser, defaultMarkdownSerializer} from "prosemirror-markdown"

import {schema, MarkdownSerializerState, MarkdownSerializer, MarkdownParser, defaultMarkdownParser, defaultMarkdownSerializer} from "prosemirror-markdown"
import {Schema} from "prosemirror-model"


module.exports = {
    MarkdownParser: MarkdownParser,
    defaultMarkdownParser: defaultMarkdownParser,
    defaultMarkdownSerializer: defaultMarkdownSerializer,
    MarkdownSerializerState: MarkdownSerializerState,
    MarkdownSerializer: MarkdownSerializer,
    schema: schema
};