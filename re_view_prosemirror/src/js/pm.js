export {EditorView, Decoration, DecorationSet} from "prosemirror-view"
export {EditorState, Selection, SelectionRange, TextSelection, NodeSelection, AllSelection, Transaction, Plugin, PluginKey} from "prosemirror-state"
export {keymap} from "prosemirror-keymap"

export {findWrapping, liftTarget, canSplit, canJoin, ReplaceAroundStep, ReplaceStep} from "prosemirror-transform"
export {wrapInList, splitListItem, liftListItem, sinkListItem} from "prosemirror-schema-list"
export {InputRule, wrappingInputRule, textblockTypeInputRule, inputRules, undoInputRule, allInputRules} from "prosemirror-inputrules"
export {Schema, Node, Mark, ResolvedPos, NodeRange, Fragment, Slice, MarkType, NodeType} from "prosemirror-model"

import * as commandsObj from "prosemirror-commands"
export const commands = commandsObj;

import * as historyObj from "prosemirror-history"
export const history = historyObj;

import * as modelObj from "prosemirror-model"
export const model = modelObj;