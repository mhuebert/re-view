var pm =
/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 50);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

exports.Node = __webpack_require__(16).Node
;var assign;
((assign = __webpack_require__(17), exports.ResolvedPos = assign.ResolvedPos, exports.NodeRange = assign.NodeRange, assign))
exports.Fragment = __webpack_require__(3).Fragment
;var assign$1;
((assign$1 = __webpack_require__(11), exports.Slice = assign$1.Slice, exports.ReplaceError = assign$1.ReplaceError, assign$1))
exports.Mark = __webpack_require__(4).Mark

;var assign$2;
((assign$2 = __webpack_require__(35), exports.Schema = assign$2.Schema, exports.NodeType = assign$2.NodeType, exports.MarkType = assign$2.MarkType, assign$2))
;var assign$3;
((assign$3 = __webpack_require__(15), exports.ContentMatch = assign$3.ContentMatch, assign$3))

exports.DOMParser = __webpack_require__(34).DOMParser
exports.DOMSerializer =  __webpack_require__(36).DOMSerializer


/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

;var assign;
((assign = __webpack_require__(18), exports.Selection = assign.Selection, exports.SelectionRange = assign.SelectionRange, exports.TextSelection = assign.TextSelection, exports.NodeSelection = assign.NodeSelection, exports.AllSelection = assign.AllSelection, assign))

exports.Transaction = __webpack_require__(19).Transaction

exports.EditorState = __webpack_require__(38).EditorState

;var assign$1;
((assign$1 = __webpack_require__(37), exports.Plugin = assign$1.Plugin, exports.PluginKey = assign$1.PluginKey, assign$1))


/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

;var assign;
((assign = __webpack_require__(9), exports.Transform = assign.Transform, exports.TransformError = assign.TransformError, assign))
;var assign$1;
((assign$1 = __webpack_require__(12), exports.Step = assign$1.Step, exports.StepResult = assign$1.StepResult, assign$1))
;var assign$2;
((assign$2 = __webpack_require__(21), exports.joinPoint = assign$2.joinPoint, exports.canJoin = assign$2.canJoin, exports.canSplit = assign$2.canSplit, exports.insertPoint = assign$2.insertPoint, exports.liftTarget = assign$2.liftTarget, exports.findWrapping = assign$2.findWrapping, assign$2))
;var assign$3;
((assign$3 = __webpack_require__(7), exports.StepMap = assign$3.StepMap, exports.MapResult = assign$3.MapResult, exports.Mapping = assign$3.Mapping, assign$3))
;var assign$4;
((assign$4 = __webpack_require__(20), exports.AddMarkStep = assign$4.AddMarkStep, exports.RemoveMarkStep = assign$4.RemoveMarkStep, assign$4))
;var assign$5;
((assign$5 = __webpack_require__(8), exports.ReplaceStep = assign$5.ReplaceStep, exports.ReplaceAroundStep = assign$5.ReplaceAroundStep, assign$5))
__webpack_require__(39)
;var assign$6;
((assign$6 = __webpack_require__(40), exports.replaceStep = assign$6.replaceStep, assign$6))


/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(33);
var findDiffStart = ref.findDiffStart;
var findDiffEnd = ref.findDiffEnd;

// ::- Fragment is the type used to represent a node's collection of
// child nodes.
//
// Fragments are persistent data structures. That means you should
// _not_ mutate them or their content, but create new instances
// whenever needed. The API tries to make this easy.
var Fragment = function(content, size) {
  var this$1 = this;

  this.content = content
  this.size = size || 0
  if (size == null) { for (var i = 0; i < content.length; i++)
    { this$1.size += content[i].nodeSize } }
};

var prototypeAccessors = { firstChild: {},lastChild: {},childCount: {} };

// :: (number, number, (node: Node, start: number, parent: Node, index: number) → ?bool)
// Invoke a callback for all descendant nodes between the given two
// positions (relative to start of this fragment).
Fragment.prototype.nodesBetween = function (from, to, f, nodeStart, parent) {
    var this$1 = this;
    if ( nodeStart === void 0 ) nodeStart = 0;

  for (var i = 0, pos = 0; pos < to; i++) {
    var child = this$1.content[i], end = pos + child.nodeSize
    if (end > from && f(child, nodeStart + pos, parent, i) !== false && child.content.size) {
      var start = pos + 1
      child.nodesBetween(Math.max(0, from - start),
                         Math.min(child.content.size, to - start),
                         f, nodeStart + start)
    }
    pos = end
  }
};

// :: ((node: Node, pos: number, parent: Node))
// Call the given callback for every descendant node.
Fragment.prototype.descendants = function (f) {
  this.nodesBetween(0, this.size, f)
};

// : (number, number, ?string, ?string) → string
Fragment.prototype.textBetween = function (from, to, blockSeparator, leafText) {
  var text = "", separated = true
  this.nodesBetween(from, to, function (node, pos) {
    if (node.isText) {
      text += node.text.slice(Math.max(from, pos) - pos, to - pos)
      separated = !blockSeparator
    } else if (node.isLeaf && leafText) {
      text += leafText
      separated = !blockSeparator
    } else if (!separated && node.isBlock) {
      text += blockSeparator
      separated = true
    }
  }, 0)
  return text
};

// :: (Fragment) → Fragment
// Create a new fragment containing the content of this fragment and
// `other`.
Fragment.prototype.append = function (other) {
  if (!other.size) { return this }
  if (!this.size) { return other }
  var last = this.lastChild, first = other.firstChild, content = this.content.slice(), i = 0
  if (last.isText && last.sameMarkup(first)) {
    content[content.length - 1] = last.withText(last.text + first.text)
    i = 1
  }
  for (; i < other.content.length; i++) { content.push(other.content[i]) }
  return new Fragment(content, this.size + other.size)
};

// :: (number, ?number) → Fragment
// Cut out the sub-fragment between the two given positions.
Fragment.prototype.cut = function (from, to) {
    var this$1 = this;

  if (to == null) { to = this.size }
  if (from == 0 && to == this.size) { return this }
  var result = [], size = 0
  if (to > from) { for (var i = 0, pos = 0; pos < to; i++) {
    var child = this$1.content[i], end = pos + child.nodeSize
    if (end > from) {
      if (pos < from || end > to) {
        if (child.isText)
          { child = child.cut(Math.max(0, from - pos), Math.min(child.text.length, to - pos)) }
        else
          { child = child.cut(Math.max(0, from - pos - 1), Math.min(child.content.size, to - pos - 1)) }
      }
      result.push(child)
      size += child.nodeSize
    }
    pos = end
  } }
  return new Fragment(result, size)
};

Fragment.prototype.cutByIndex = function (from, to) {
  if (from == to) { return Fragment.empty }
  if (from == 0 && to == this.content.length) { return this }
  return new Fragment(this.content.slice(from, to))
};

// :: (number, Node) → Fragment
// Create a new fragment in which the node at the given index is
// replaced by the given node.
Fragment.prototype.replaceChild = function (index, node) {
  var current = this.content[index]
  if (current == node) { return this }
  var copy = this.content.slice()
  var size = this.size + node.nodeSize - current.nodeSize
  copy[index] = node
  return new Fragment(copy, size)
};

// : (Node) → Fragment
// Create a new fragment by prepending the given node to this
// fragment.
Fragment.prototype.addToStart = function (node) {
  return new Fragment([node].concat(this.content), this.size + node.nodeSize)
};

// : (Node) → Fragment
// Create a new fragment by appending the given node to this
// fragment.
Fragment.prototype.addToEnd = function (node) {
  return new Fragment(this.content.concat(node), this.size + node.nodeSize)
};

// :: (Fragment) → bool
// Compare this fragment to another one.
Fragment.prototype.eq = function (other) {
    var this$1 = this;

  if (this.content.length != other.content.length) { return false }
  for (var i = 0; i < this.content.length; i++)
    { if (!this$1.content[i].eq(other.content[i])) { return false } }
  return true
};

// :: ?Node
// The first child of the fragment, or `null` if it is empty.
prototypeAccessors.firstChild.get = function () { return this.content.length ? this.content[0] : null };

// :: ?Node
// The last child of the fragment, or `null` if it is empty.
prototypeAccessors.lastChild.get = function () { return this.content.length ? this.content[this.content.length - 1] : null };

// :: number
// The number of child nodes in this fragment.
prototypeAccessors.childCount.get = function () { return this.content.length };

// :: (number) → Node
// Get the child node at the given index. Raise an error when the
// index is out of range.
Fragment.prototype.child = function (index) {
  var found = this.content[index]
  if (!found) { throw new RangeError("Index " + index + " out of range for " + this) }
  return found
};

// :: (number) → number
// Get the offset at (size of children before) the given index.
Fragment.prototype.offsetAt = function (index) {
    var this$1 = this;

  var offset = 0
  for (var i = 0; i < index; i++) { offset += this$1.content[i].nodeSize }
  return offset
};

// :: (number) → ?Node
// Get the child node at the given index, if it exists.
Fragment.prototype.maybeChild = function (index) {
  return this.content[index]
};

// :: ((node: Node, offset: number, index: number))
// Call `f` for every child node, passing the node, its offset
// into this parent node, and its index.
Fragment.prototype.forEach = function (f) {
    var this$1 = this;

  for (var i = 0, p = 0; i < this.content.length; i++) {
    var child = this$1.content[i]
    f(child, p, i)
    p += child.nodeSize
  }
};

// :: (Fragment) → ?number
// Find the first position at which this fragment and another
// fragment differ, or `null` if they are the same.
Fragment.prototype.findDiffStart = function (other, pos) {
    if ( pos === void 0 ) pos = 0;

  return findDiffStart(this, other, pos)
};

// :: (Node) → ?{a: number, b: number}
// Find the first position, searching from the end, at which this
// fragment and the given fragment differ, or `null` if they are the
// same. Since this position will not be the same in both nodes, an
// object with two separate positions is returned.
Fragment.prototype.findDiffEnd = function (other, pos, otherPos) {
    if ( pos === void 0 ) pos = this.size;
    if ( otherPos === void 0 ) otherPos = other.size;

  return findDiffEnd(this, other, pos, otherPos)
};

// : (number, ?number) → {index: number, offset: number}
// Find the index and inner offset corresponding to a given relative
// position in this fragment. The result object will be reused
// (overwritten) the next time the function is called. (Not public.)
Fragment.prototype.findIndex = function (pos, round) {
    var this$1 = this;
    if ( round === void 0 ) round = -1;

  if (pos == 0) { return retIndex(0, pos) }
  if (pos == this.size) { return retIndex(this.content.length, pos) }
  if (pos > this.size || pos < 0) { throw new RangeError(("Position " + pos + " outside of fragment (" + (this) + ")")) }
  for (var i = 0, curPos = 0;; i++) {
    var cur = this$1.child(i), end = curPos + cur.nodeSize
    if (end >= pos) {
      if (end == pos || round > 0) { return retIndex(i + 1, end) }
      return retIndex(i, curPos)
    }
    curPos = end
  }
};

// :: () → string
// Return a debugging string that describes this fragment.
Fragment.prototype.toString = function () { return "<" + this.toStringInner() + ">" };

Fragment.prototype.toStringInner = function () { return this.content.join(", ") };

// :: () → ?Object
// Create a JSON-serializeable representation of this fragment.
Fragment.prototype.toJSON = function () {
  return this.content.length ? this.content.map(function (n) { return n.toJSON(); }) : null
};

// :: (Schema, ?Object) → Fragment
// Deserialize a fragment from its JSON representation.
Fragment.fromJSON = function (schema, value) {
  return value ? new Fragment(value.map(schema.nodeFromJSON)) : Fragment.empty
};

// :: ([Node]) → Fragment
// Build a fragment from an array of nodes. Ensures that adjacent
// text nodes with the same style are joined together.
Fragment.fromArray = function (array) {
  if (!array.length) { return Fragment.empty }
  var joined, size = 0
  for (var i = 0; i < array.length; i++) {
    var node = array[i]
    size += node.nodeSize
    if (i && node.isText && array[i - 1].sameMarkup(node)) {
      if (!joined) { joined = array.slice(0, i) }
      joined[joined.length - 1] = node.withText(joined[joined.length - 1].text + node.text)
    } else if (joined) {
      joined.push(node)
    }
  }
  return new Fragment(joined || array, size)
};

// :: (?union<Fragment, Node, [Node]>) → Fragment
// Create a fragment from something that can be interpreted as a set
// of nodes. For `null`, it returns the empty fragment. For a
// fragment, the fragment itself. For a node or array of nodes, a
// fragment containing those nodes.
Fragment.from = function (nodes) {
  if (!nodes) { return Fragment.empty }
  if (nodes instanceof Fragment) { return nodes }
  if (Array.isArray(nodes)) { return this.fromArray(nodes) }
  return new Fragment([nodes], nodes.nodeSize)
};

Object.defineProperties( Fragment.prototype, prototypeAccessors );
exports.Fragment = Fragment

var found = {index: 0, offset: 0}
function retIndex(index, offset) {
  found.index = index
  found.offset = offset
  return found
}

// :: Fragment
// An empty fragment. Intended to be reused whenever a node doesn't
// contain anything (rather than allocating a new empty fragment for
// each leaf node).
Fragment.empty = new Fragment([], 0)


/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(14);
var compareDeep = ref.compareDeep;

// ::- A mark is a piece of information that can be attached to a node,
// such as it being emphasized, in code font, or a link. It has a type
// and optionally a set of attributes that provide further information
// (such as the target of the link). Marks are created through a
// `Schema`, which controls which types exist and which
// attributes they have.
var Mark = function(type, attrs) {
  // :: MarkType
  // The type of this mark.
  this.type = type
  // :: Object
  // The attributes associated with this mark.
  this.attrs = attrs
};

// :: ([Mark]) → [Mark]
// Given a set of marks, create a new set which contains this one as
// well, in the right position. If this mark is already in the set,
// the set itself is returned. If a mark of this type with different
// attributes is already in the set, a set in which it is replaced
// by this one is returned.
Mark.prototype.addToSet = function (set) {
    var this$1 = this;

  var copy, placed = false
  for (var i = 0; i < set.length; i++) {
    var other = set[i]
    if (this$1.eq(other)) { return set }
    if (this$1.type.excludes(other.type)) {
      if (!copy) { copy = set.slice(0, i) }
    } else if (other.type.excludes(this$1.type)) {
      return set
    } else {
      if (!placed && other.type.rank > this$1.type.rank) {
        if (!copy) { copy = set.slice(0, i) }
        copy.push(this$1)
        placed = true
      }
      if (copy) { copy.push(other) }
    }
  }
  if (!copy) { copy = set.slice() }
  if (!placed) { copy.push(this) }
  return copy
};

// :: ([Mark]) → [Mark]
// Remove this mark from the given set, returning a new set. If this
// mark is not in the set, the set itself is returned.
Mark.prototype.removeFromSet = function (set) {
    var this$1 = this;

  for (var i = 0; i < set.length; i++)
    { if (this$1.eq(set[i]))
      { return set.slice(0, i).concat(set.slice(i + 1)) } }
  return set
};

// :: ([Mark]) → bool
// Test whether this mark is in the given set of marks.
Mark.prototype.isInSet = function (set) {
    var this$1 = this;

  for (var i = 0; i < set.length; i++)
    { if (this$1.eq(set[i])) { return true } }
  return false
};

// :: (Mark) → bool
// Test whether this mark has the same type and attributes as
// another mark.
Mark.prototype.eq = function (other) {
  return this == other ||
    (this.type == other.type && compareDeep(this.attrs, other.attrs))
};

// :: () → Object
// Convert this mark to a JSON-serializeable representation.
Mark.prototype.toJSON = function () {
    var this$1 = this;

  var obj = {type: this.type.name}
  for (var _ in this$1.attrs) {
    obj.attrs = this$1.attrs
    break
  }
  return obj
};

// :: (Schema, Object) → Mark
Mark.fromJSON = function (schema, json) {
  var type = schema.marks[json.type]
  if (!type) { throw new RangeError(("There is no mark type " + (json.type) + " in this schema")) }
  return type.create(json.attrs)
};

// :: ([Mark], [Mark]) → bool
// Test whether two sets of marks are identical.
Mark.sameSet = function (a, b) {
  if (a == b) { return true }
  if (a.length != b.length) { return false }
  for (var i = 0; i < a.length; i++)
    { if (!a[i].eq(b[i])) { return false } }
  return true
};

// :: (?union<Mark, [Mark]>) → [Mark]
// Create a properly sorted mark set from null, a single mark, or an
// unsorted array of marks.
Mark.setFrom = function (marks) {
  if (!marks || marks.length == 0) { return Mark.none }
  if (marks instanceof Mark) { return [marks] }
  var copy = marks.slice()
  copy.sort(function (a, b) { return a.type.rank - b.type.rank; })
  return copy
};
exports.Mark = Mark

// :: [Mark] The empty set of marks.
Mark.none = []


/***/ }),
/* 5 */
/***/ (function(module, exports) {

var result = module.exports = {}

if (typeof navigator != "undefined") {
  var ie_edge = /Edge\/(\d+)/.exec(navigator.userAgent)
  var ie_upto10 = /MSIE \d/.test(navigator.userAgent)
  var ie_11up = /Trident\/(?:[7-9]|\d{2,})\..*rv:(\d+)/.exec(navigator.userAgent)

  result.mac = /Mac/.test(navigator.platform)
  var ie = result.ie = !!(ie_upto10 || ie_11up || ie_edge)
  result.ie_version = ie_upto10 ? document.documentMode || 6 : ie_11up ? +ie_11up[1] : ie_edge ? +ie_edge[1] : null
  result.gecko = !ie && /gecko\/\d/i.test(navigator.userAgent)
  result.ios = !ie && /AppleWebKit/.test(navigator.userAgent) && /Mobile\/\w+/.test(navigator.userAgent)
  result.webkit = !ie && 'WebkitAppearance' in document.documentElement.style
}


/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

var browser = __webpack_require__(5)

var domIndex = exports.domIndex = function(node) {
  for (var index = 0;; index++) {
    node = node.previousSibling
    if (!node) { return index }
  }
}

exports.parentNode = function(node) {
  var parent = node.parentNode
  return parent && parent.nodeType == 11 ? parent.host : parent
}

exports.textRange = function(node, from, to) {
  var range = document.createRange()
  range.setEnd(node, to == null ? node.nodeValue.length : to)
  range.setStart(node, from || 0)
  return range
}

// Scans forward and backward through DOM positions equivalent to the
// given one to see if the two are in the same place (i.e. after a
// text node vs at the end of that text node)
exports.isEquivalentPosition = function(node, off, targetNode, targetOff) {
  return targetNode && (scanFor(node, off, targetNode, targetOff, -1) ||
                        scanFor(node, off, targetNode, targetOff, 1))
}

function scanFor(node, off, targetNode, targetOff, dir) {
  for (;;) {
    if (node == targetNode && off == targetOff) { return true }
    if (off == (dir < 0 ? 0 : nodeSize(node))) {
      var parent = node.parentNode
      if (parent.nodeType != 1 || hasBlockDesc(parent)) { return false }
      off = domIndex(node) + (dir < 0 ? 0 : 1)
      node = parent
    } else if (node.nodeType == 1) {
      node = node.childNodes[off + (dir < 0 ? -1 : 0)]
      off = dir < 0 ? nodeSize(node) : 0
    } else {
      return false
    }
  }
}

function nodeSize(node) {
  return node.nodeType == 3 ? node.nodeValue.length : node.childNodes.length
}

function hasBlockDesc(dom) {
  var desc = dom.pmViewDesc
  return desc && desc.node && desc.node.isBlock
}

// Work around Chrome issue https://bugs.chromium.org/p/chromium/issues/detail?id=447523
// (isCollapsed inappropriately returns true in shadow dom)
exports.selectionCollapsed = function(domSel) {
  var collapsed = domSel.isCollapsed
  if (collapsed && browser.chrome && domSel.rangeCount && !domSel.getRangeAt(0).collapsed)
    { collapsed = false }
  return collapsed
}


/***/ }),
/* 7 */
/***/ (function(module, exports) {

// Mappable:: interface
// There are several things that positions can be mapped through.
// We'll denote those as 'mappable'.
//
//   map:: (pos: number, assoc: ?number) → number
//   Map a position through this object. When given, `assoc` (should
//   be -1 or 1, defaults to 1) determines with which side the
//   position is associated, which determines in which direction to
//   move when a chunk of content is inserted at the mapped position,
//   and when to consider the position to be deleted.
//
//   mapResult:: (pos: number, assoc: ?number) → MapResult
//   Map a position, and return an object containing additional
//   information about the mapping. The result's `deleted` field tells
//   you whether the position was deleted (completely enclosed in a
//   replaced range) during the mapping.

// Recovery values encode a range index and an offset. They are
// represented as numbers, because tons of them will be created when
// mapping, for example, a large number of marked ranges. The number's
// lower 16 bits provide the index, the remaining bits the offset.
//
// Note: We intentionally don't use bit shift operators to en- and
// decode these, since those clip to 32 bits, which we might in rare
// cases want to overflow. A 64-bit float can represent 48-bit
// integers precisely.

var lower16 = 0xffff
var factor16 = Math.pow(2, 16)

function makeRecover(index, offset) { return index + offset * factor16 }
function recoverIndex(value) { return value & lower16 }
function recoverOffset(value) { return (value - (value & lower16)) / factor16 }

// ::- An object representing a mapped position with extra
// information.
var MapResult = function MapResult(pos, deleted, recover) {
  if ( deleted === void 0 ) deleted = false;
  if ( recover === void 0 ) recover = null;

  // :: number The mapped version of the position.
  this.pos = pos
  // :: bool Tells you whether the position was deleted, that is,
  // whether the step removed its surroundings from the document.
  this.deleted = deleted
  this.recover = recover
};
exports.MapResult = MapResult

// ::- A map describing the deletions and insertions made by a step,
// which can be used to find the correspondence between positions in
// the pre-step version of a document and the same position in the
// post-step version. This class implements [`Mappable`](#transform.Mappable).
var StepMap = function StepMap(ranges, inverted) {
  if ( inverted === void 0 ) inverted = false;

  this.ranges = ranges
  this.inverted = inverted
};

StepMap.prototype.recover = function recover (value) {
    var this$1 = this;

  var diff = 0, index = recoverIndex(value)
  if (!this.inverted) { for (var i = 0; i < index; i++)
    { diff += this$1.ranges[i * 3 + 2] - this$1.ranges[i * 3 + 1] } }
  return this.ranges[index * 3] + diff + recoverOffset(value)
};

// :: (number, ?number) → MapResult
// Map the given position through this map. The `assoc` parameter can
// be used to control what happens when the transform inserted
// content at (or around) this position—if `assoc` is negative, the a
// position before the inserted content will be returned, if it is
// positive, a position after the insertion is returned.
StepMap.prototype.mapResult = function mapResult (pos, assoc) { return this._map(pos, assoc, false) };

// :: (number, ?number) → number
// Map the given position through this map, returning only the
// mapped position.
StepMap.prototype.map = function map (pos, assoc) { return this._map(pos, assoc, true) };

StepMap.prototype._map = function _map (pos, assoc, simple) {
    var this$1 = this;

  var diff = 0, oldIndex = this.inverted ? 2 : 1, newIndex = this.inverted ? 1 : 2
  for (var i = 0; i < this.ranges.length; i += 3) {
    var start = this$1.ranges[i] - (this$1.inverted ? diff : 0)
    if (start > pos) { break }
    var oldSize = this$1.ranges[i + oldIndex], newSize = this$1.ranges[i + newIndex], end = start + oldSize
    if (pos <= end) {
      var side = !oldSize ? assoc : pos == start ? -1 : pos == end ? 1 : assoc
      var result = start + diff + (side < 0 ? 0 : newSize)
      if (simple) { return result }
      var recover = makeRecover(i / 3, pos - start)
      return new MapResult(result, assoc < 0 ? pos != start : pos != end, recover)
    }
    diff += newSize - oldSize
  }
  return simple ? pos + diff : new MapResult(pos + diff)
};

StepMap.prototype.touches = function touches (pos, recover) {
    var this$1 = this;

  var diff = 0, index = recoverIndex(recover)
  var oldIndex = this.inverted ? 2 : 1, newIndex = this.inverted ? 1 : 2
  for (var i = 0; i < this.ranges.length; i += 3) {
    var start = this$1.ranges[i] - (this$1.inverted ? diff : 0)
    if (start > pos) { break }
    var oldSize = this$1.ranges[i + oldIndex], end = start + oldSize
    if (pos <= end && i == index * 3) { return true }
    diff += this$1.ranges[i + newIndex] - oldSize
  }
  return false
};

// :: ((oldStart: number, oldEnd: number, newStart: number, newEnd: number))
// Calls the given function on each of the changed ranges denoted by
// this map.
StepMap.prototype.forEach = function forEach (f) {
    var this$1 = this;

  var oldIndex = this.inverted ? 2 : 1, newIndex = this.inverted ? 1 : 2
  for (var i = 0, diff = 0; i < this.ranges.length; i += 3) {
    var start = this$1.ranges[i], oldStart = start - (this$1.inverted ? diff : 0), newStart = start + (this$1.inverted ? 0 : diff)
    var oldSize = this$1.ranges[i + oldIndex], newSize = this$1.ranges[i + newIndex]
    f(oldStart, oldStart + oldSize, newStart, newStart + newSize)
    diff += newSize - oldSize
  }
};

// :: () → StepMap
// Create an inverted version of this map. The result can be used to
// map positions in the post-step document to the pre-step document.
StepMap.prototype.invert = function invert () {
  return new StepMap(this.ranges, !this.inverted)
};

StepMap.prototype.toString = function toString () {
  return (this.inverted ? "-" : "") + JSON.stringify(this.ranges)
};
exports.StepMap = StepMap

StepMap.empty = new StepMap([])

// ::- A mapping represents a pipeline of zero or more [step
// maps](#transform.StepMap). It has special provisions for losslessly
// handling mapping positions through a series of steps in which some
// steps are inverted versions of earlier steps. (This comes up when
// ‘rebasing’ steps for collaboration or history management.) This
// class implements [`Mappable`](#transform.Mappable).
var Mapping = function Mapping(maps, mirror, from, to) {
  // :: [StepMap]
  // The step maps in this mapping.
  this.maps = maps || []
  // :: number
  // The starting position in the `maps` array, used when `map` or
  // `mapResult` is called.
  this.from = from || 0
  // :: number
  // The end positions in the `maps` array.
  this.to = to == null ? this.maps.length : to
  this.mirror = mirror
};

// :: (?number, ?number) → Mapping
// Create a mapping that maps only through a part of this one.
Mapping.prototype.slice = function slice (from, to) {
    if ( from === void 0 ) from = 0;
    if ( to === void 0 ) to = this.maps.length;

  return new Mapping(this.maps, this.mirror, from, to)
};

Mapping.prototype.copy = function copy () {
  return new Mapping(this.maps.slice(), this.mirror && this.mirror.slice(), this.from, this.to)
};

Mapping.prototype.getMirror = function getMirror (n) {
    var this$1 = this;

  if (this.mirror) { for (var i = 0; i < this.mirror.length; i++)
    { if (this$1.mirror[i] == n) { return this$1.mirror[i + (i % 2 ? -1 : 1)] } } }
};

Mapping.prototype.setMirror = function setMirror (n, m) {
  if (!this.mirror) { this.mirror = [] }
  this.mirror.push(n, m)
};

// :: (StepMap, ?number)
// Add a step map to the end of this mapping. If `mirrors` is
// given, it should be the index of the step map that is the mirror
// image of this one.
Mapping.prototype.appendMap = function appendMap (map, mirrors) {
  this.to = this.maps.push(map)
  if (mirrors != null) { this.setMirror(this.maps.length - 1, mirrors) }
};

// :: (Mapping)
// Add all the step maps in a given mapping to this one (preserving
// mirroring information).
Mapping.prototype.appendMapping = function appendMapping (mapping) {
    var this$1 = this;

  for (var i = 0, startSize = this.maps.length; i < mapping.maps.length; i++) {
    var mirr = mapping.getMirror(i)
    this$1.appendMap(mapping.maps[i], mirr != null && mirr < i ? startSize + mirr : null)
  }
};

// :: (Mapping)
// Append the inverse of the given mapping to this one.
Mapping.prototype.appendMappingInverted = function appendMappingInverted (mapping) {
    var this$1 = this;

  for (var i = mapping.maps.length - 1, totalSize = this.maps.length + mapping.maps.length; i >= 0; i--) {
    var mirr = mapping.getMirror(i)
    this$1.appendMap(mapping.maps[i].invert(), mirr != null && mirr > i ? totalSize - mirr - 1 : null)
  }
};

// () → Mapping
// Create an inverted version of this mapping.
Mapping.prototype.invert = function invert () {
  var inverse = new Mapping
  inverse.appendMappingInverted(this)
  return inverse
};

// :: (number, ?number) → number
// Map a position through this mapping.
Mapping.prototype.map = function map (pos, assoc) {
    var this$1 = this;

  if (this.mirror) { return this._map(pos, assoc, true) }
  for (var i = this.from; i < this.to; i++)
    { pos = this$1.maps[i].map(pos, assoc) }
  return pos
};

// :: (number, ?number) → MapResult
// Map a position through this mapping, returning a mapping
// result.
Mapping.prototype.mapResult = function mapResult (pos, assoc) { return this._map(pos, assoc, false) };

Mapping.prototype._map = function _map (pos, assoc, simple) {
    var this$1 = this;

  var deleted = false, recoverables = null

  for (var i = this.from; i < this.to; i++) {
    var map = this$1.maps[i], rec = recoverables && recoverables[i]
    if (rec != null && map.touches(pos, rec)) {
      pos = map.recover(rec)
      continue
    }

    var result = map.mapResult(pos, assoc)
    if (result.recover != null) {
      var corr = this$1.getMirror(i)
      if (corr != null && corr > i && corr < this$1.to) {
        if (result.deleted) {
          i = corr
          pos = this$1.maps[corr].recover(result.recover)
          continue
        } else {
          ;(recoverables || (recoverables = Object.create(null)))[corr] = result.recover
        }
      }
    }

    if (result.deleted) { deleted = true }
    pos = result.pos
  }

  return simple ? pos : new MapResult(pos, deleted)
};
exports.Mapping = Mapping


/***/ }),
/* 8 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Slice = ref.Slice;

var ref$1 = __webpack_require__(12);
var Step = ref$1.Step;
var StepResult = ref$1.StepResult;
var ref$2 = __webpack_require__(7);
var StepMap = ref$2.StepMap;

// ::- Replace a part of the document with a slice of new content.
var ReplaceStep = (function (Step) {
  function ReplaceStep(from, to, slice, structure) {
    Step.call(this)
    this.from = from
    this.to = to
    this.slice = slice
    this.structure = !!structure
  }

  if ( Step ) ReplaceStep.__proto__ = Step;
  ReplaceStep.prototype = Object.create( Step && Step.prototype );
  ReplaceStep.prototype.constructor = ReplaceStep;

  ReplaceStep.prototype.apply = function apply (doc) {
    if (this.structure && contentBetween(doc, this.from, this.to))
      { return StepResult.fail("Structure replace would overwrite content") }
    return StepResult.fromReplace(doc, this.from, this.to, this.slice)
  };

  ReplaceStep.prototype.getMap = function getMap () {
    return new StepMap([this.from, this.to - this.from, this.slice.size])
  };

  ReplaceStep.prototype.invert = function invert (doc) {
    return new ReplaceStep(this.from, this.from + this.slice.size, doc.slice(this.from, this.to))
  };

  ReplaceStep.prototype.map = function map (mapping) {
    var from = mapping.mapResult(this.from, 1), to = mapping.mapResult(this.to, -1)
    if (from.deleted && to.deleted) { return null }
    return new ReplaceStep(from.pos, Math.max(from.pos, to.pos), this.slice)
  };

  ReplaceStep.prototype.merge = function merge (other) {
    if (!(other instanceof ReplaceStep) || other.structure != this.structure) { return null }

    if (this.from + this.slice.size == other.from && !this.slice.openRight && !other.slice.openLeft) {
      var slice = this.slice.size + other.slice.size == 0 ? Slice.empty
          : new Slice(this.slice.content.append(other.slice.content), this.slice.openLeft, other.slice.openRight)
      return new ReplaceStep(this.from, this.to + (other.to - other.from), slice, this.structure)
    } else if (other.to == this.from && !this.slice.openLeft && !other.slice.openRight) {
      var slice$1 = this.slice.size + other.slice.size == 0 ? Slice.empty
          : new Slice(other.slice.content.append(this.slice.content), other.slice.openLeft, this.slice.openRight)
      return new ReplaceStep(other.from, this.to, slice$1, this.structure)
    } else {
      return null
    }
  };

  ReplaceStep.prototype.toJSON = function toJSON () {
    var json = {stepType: "replace", from: this.from, to: this.to}
    if (this.slice.size) { json.slice = this.slice.toJSON() }
    if (this.structure) { json.structure = true }
    return json
  };

  ReplaceStep.prototype.offset = function offset (n) {
    return new ReplaceStep(this.from + n, this.to + n, this.slice, this.structure)
  };

  ReplaceStep.fromJSON = function fromJSON (schema, json) {
    return new ReplaceStep(json.from, json.to, Slice.fromJSON(schema, json.slice), !!json.structure)
  };

  return ReplaceStep;
}(Step));
exports.ReplaceStep = ReplaceStep

Step.jsonID("replace", ReplaceStep)

// ::- Replace a part of the document with a slice of content, but
// preserve a range of the replaced content by moving it into the
// slice.
var ReplaceAroundStep = (function (Step) {
  function ReplaceAroundStep(from, to, gapFrom, gapTo, slice, insert, structure) {
    Step.call(this)
    this.from = from
    this.to = to
    this.gapFrom = gapFrom
    this.gapTo = gapTo
    this.slice = slice
    this.insert = insert
    this.structure = !!structure
  }

  if ( Step ) ReplaceAroundStep.__proto__ = Step;
  ReplaceAroundStep.prototype = Object.create( Step && Step.prototype );
  ReplaceAroundStep.prototype.constructor = ReplaceAroundStep;

  ReplaceAroundStep.prototype.apply = function apply (doc) {
    if (this.structure && (contentBetween(doc, this.from, this.gapFrom) ||
                           contentBetween(doc, this.gapTo, this.to)))
      { return StepResult.fail("Structure gap-replace would overwrite content") }

    var gap = doc.slice(this.gapFrom, this.gapTo)
    if (gap.openLeft || gap.openRight)
      { return StepResult.fail("Gap is not a flat range") }
    var inserted = this.slice.insertAt(this.insert, gap.content)
    if (!inserted) { return StepResult.fail("Content does not fit in gap") }
    return StepResult.fromReplace(doc, this.from, this.to, inserted)
  };

  ReplaceAroundStep.prototype.getMap = function getMap () {
    return new StepMap([this.from, this.gapFrom - this.from, this.insert,
                        this.gapTo, this.to - this.gapTo, this.slice.size - this.insert])
  };

  ReplaceAroundStep.prototype.invert = function invert (doc) {
    var gap = this.gapTo - this.gapFrom
    return new ReplaceAroundStep(this.from, this.from + this.slice.size + gap,
                                 this.from + this.insert, this.from + this.insert + gap,
                                 doc.slice(this.from, this.to).removeBetween(this.gapFrom - this.from, this.gapTo - this.from),
                                 this.gapFrom - this.from, this.structure)
  };

  ReplaceAroundStep.prototype.map = function map (mapping) {
    var from = mapping.mapResult(this.from, 1), to = mapping.mapResult(this.to, -1)
    var gapFrom = mapping.map(this.gapFrom, -1), gapTo = mapping.map(this.gapTo, 1)
    if ((from.deleted && to.deleted) || gapFrom < from.pos || gapTo > to.pos) { return null }
    return new ReplaceAroundStep(from.pos, to.pos, gapFrom, gapTo, this.slice, this.insert, this.structure)
  };

  ReplaceAroundStep.toJSON = function toJSON () {
    var json = {stepType: "replaceAround", from: this.from, to: this.to,
                gapFrom: this.gapFrom, gapTo: this.gapTo, slice: this.slice.toJSON()}
    if (this.structure) { json.structure = true }
    return true
  };

  ReplaceAroundStep.prototype.offset = function offset (n) {
    return new ReplaceAroundStep(this.from + n, this.to + n, this.gapFrom + n, this.gapTo + n,
                                 this.slice, this.insert, this.structure)
  };

  ReplaceAroundStep.fromJSON = function fromJSON (schema, json) {
    return new ReplaceAroundStep(json.from, json.to, json.gapFrom, json.gapTo,
                                 Slice.fromJSON(schema, json.slice), json.insert, !!json.structure)
  };

  return ReplaceAroundStep;
}(Step));
exports.ReplaceAroundStep = ReplaceAroundStep

Step.jsonID("replaceAround", ReplaceAroundStep)

function contentBetween(doc, from, to) {
  var $from = doc.resolve(from), dist = to - from, depth = $from.depth
  while (dist > 0 && depth > 0 && $from.indexAfter(depth) == $from.node(depth).childCount) {
    depth--
    dist--
  }
  if (dist > 0) {
    var next = $from.node(depth).maybeChild($from.indexAfter(depth))
    while (dist > 0) {
      if (!next || next.isLeaf) { return true }
      next = next.firstChild
      dist--
    }
  }
  return false
}


/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(7);
var Mapping = ref.Mapping;

var TransformError = (function (Error) {
  function TransformError(message) { Error.call(this, message) }

  if ( Error ) TransformError.__proto__ = Error;
  TransformError.prototype = Object.create( Error && Error.prototype );
  TransformError.prototype.constructor = TransformError;

  var prototypeAccessors = { name: {} };
  prototypeAccessors.name.get = function () { return "TransformError" };

  Object.defineProperties( TransformError.prototype, prototypeAccessors );

  return TransformError;
}(Error));
exports.TransformError = TransformError

// ::- Abstraction to build up and track such an array of
// [steps](#transform.Step).
//
// The high-level transforming methods return the `Transform` object
// itself, so that they can be chained.
var Transform = function Transform(doc) {
  // :: Node
  // The current document (the result of applying the steps in the
  // transform).
  this.doc = doc
  // :: [Step]
  // The steps in this transform.
  this.steps = []
  // :: [Node]
  // The documents before each of the steps.
  this.docs = []
  // :: Mapping
  // A mapping with the maps for each of the steps in this transform.
  this.mapping = new Mapping
};

var prototypeAccessors$1 = { before: {},docChanged: {} };

// :: Node The document at the start of the transformation.
prototypeAccessors$1.before.get = function () { return this.docs.length ? this.docs[0] : this.doc };

// :: (step: Step) → Transform
// Apply a new step in this transformation, saving the result.
// Throws an error when the step fails.
Transform.prototype.step = function step (object) {
  var result = this.maybeStep(object)
  if (result.failed) { throw new TransformError(result.failed) }
  return this
};

// :: (Step) → StepResult
// Try to apply a step in this transformation, ignoring it if it
// fails. Returns the step result.
Transform.prototype.maybeStep = function maybeStep (step) {
  var result = step.apply(this.doc)
  if (!result.failed) { this.addStep(step, result.doc) }
  return result
};

// :: bool
// True when this transaction changes the document.
prototypeAccessors$1.docChanged.get = function () {
  return this.steps.length > 0
};

Transform.prototype.addStep = function addStep (step, doc) {
  this.docs.push(this.doc)
  this.steps.push(step)
  this.mapping.appendMap(step.getMap())
  this.doc = doc
};

Object.defineProperties( Transform.prototype, prototypeAccessors$1 );
exports.Transform = Transform


/***/ }),
/* 10 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(1);
var Plugin = ref.Plugin;

// ::- Input rules are regular expressions describing a piece of text
// that, when typed, causes something to happen. This might be
// changing two dashes into an emdash, wrapping a paragraph starting
// with `"> "` into a blockquote, or something entirely different.
var InputRule = function InputRule(match, handler) {
  this.match = match
  this.handler = typeof handler == "string" ? stringHandler(handler) : handler
};
exports.InputRule = InputRule

function stringHandler(string) {
  return function(state, match, start, end) {
    var insert = string
    if (match[1]) {
      var offset = match[0].lastIndexOf(match[1])
      insert += match[0].slice(offset + match[1].length)
      start += offset
      var cutOff = start - end
      if (cutOff > 0) {
        insert = match[0].slice(offset - cutOff, offset) + insert
        start = end
      }
    }
    var marks = state.doc.resolve(start).marks()
    return state.tr.replaceWith(start, end, state.schema.text(insert, marks))
  }
}

var MAX_MATCH = 500

// :: (config: {rules: [InputRule]}) → Plugin
// Create an input rules plugin. When enabled, it will cause text
// input that matches any of the given rules to trigger the rule's
// action, and binds the backspace key, when applied directly after an
// input rule triggered, to undo the rule's effect.
function inputRules(ref) {
  var rules = ref.rules;

  return new Plugin({
    state: {
      init: function init() { return null },
      apply: function apply(tr, prev) {
        var stored = tr.getMeta(this)
        if (stored) { return stored }
        return tr.selectionSet || tr.docChanged ? null : prev
      }
    },

    props: {
      handleTextInput: function handleTextInput(view, from, to, text) {
        var this$1 = this;

        var state = view.state, $from = state.doc.resolve(from)
        var textBefore = $from.parent.textBetween(Math.max(0, $from.parentOffset - MAX_MATCH), $from.parentOffset,
                                                  null, "\ufffc") + text
        for (var i = 0; i < rules.length; i++) {
          var match = rules[i].match.exec(textBefore)
          var tr = match && rules[i].handler(state, match, from - (match[0].length - text.length), to)
          if (!tr) { continue }
          view.dispatch(tr.setMeta(this$1, {transform: tr, from: from, to: to, text: text}))
          return true
        }
        return false
      }
    },

    isInputRules: true
  })
}
exports.inputRules = inputRules

// :: (EditorState, ?(Transaction)) → bool
// Command that will undo an input rule, if it applied to the last
// thing that the user did.
function undoInputRule(state, dispatch) {
  var plugins = state.plugins
  for (var i = 0; i < plugins.length; i++) {
    var plugin = plugins[i], undoable = (void 0)
    if (plugin.spec.isInputRules && (undoable = plugin.getState(state))) {
      if (dispatch) {
        var tr = state.tr, toUndo = undoable.transform
        for (var j = toUndo.steps.length - 1; j >= 0; j--)
          { tr.step(toUndo.steps[j].invert(toUndo.docs[j])) }
        var marks = tr.doc.resolve(undoable.from).marks()
        dispatch(tr.replaceWith(undoable.from, undoable.to, state.schema.text(undoable.text, marks)))
      }
      return true
    }
  }
  return false
}
exports.undoInputRule = undoInputRule


/***/ }),
/* 11 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(3);
var Fragment = ref.Fragment;

// ::- Error type raised by [`Node.replace`](#model.Node.replace) when
// given an invalid replacement.
var ReplaceError = (function (Error) {
  function ReplaceError(message) {
    Error.call(this, message)
    this.message = message
  }

  if ( Error ) ReplaceError.__proto__ = Error;
  ReplaceError.prototype = Object.create( Error && Error.prototype );
  ReplaceError.prototype.constructor = ReplaceError;

  var prototypeAccessors = { name: {} };
  prototypeAccessors.name.get = function () { return "ReplaceError" };

  Object.defineProperties( ReplaceError.prototype, prototypeAccessors );

  return ReplaceError;
}(Error));
exports.ReplaceError = ReplaceError

// ::- A slice represents a piece cut out of a larger document. It
// stores not only a fragment, but also the depth up to which nodes on
// both side are 'open' / cut through.
var Slice = function(content, openLeft, openRight) {
  // :: Fragment The slice's content nodes.
  this.content = content
  // :: number The open depth at the start.
  this.openLeft = openLeft
  // :: number The open depth at the end.
  this.openRight = openRight
};

var prototypeAccessors$1 = { size: {} };

// :: number
// The size this slice would add when inserted into a document.
prototypeAccessors$1.size.get = function () {
  return this.content.size - this.openLeft - this.openRight
};

Slice.prototype.insertAt = function (pos, fragment) {
  var content = insertInto(this.content, pos + this.openLeft, fragment, null)
  return content && new Slice(content, this.openLeft, this.openRight)
};

Slice.prototype.removeBetween = function (from, to) {
  return new Slice(removeRange(this.content, from + this.openLeft, to + this.openLeft), this.openLeft, this.openRight)
};

// :: (Slice) → bool
// Tests whether this slice is equal to another slice.
Slice.prototype.eq = function (other) {
  return this.content.eq(other.content) && this.openLeft == other.openLeft && this.openRight == other.openRight
};

Slice.prototype.toString = function () {
  return this.content + "(" + this.openLeft + "," + this.openRight + ")"
};

// :: () → ?Object
// Convert a slice to a JSON-serializable representation.
Slice.prototype.toJSON = function () {
  if (!this.content.size) { return null }
  return {content: this.content.toJSON(),
          openLeft: this.openLeft,
          openRight: this.openRight}
};

// :: (Schema, ?Object) → Slice
// Deserialize a slice from its JSON representation.
Slice.fromJSON = function (schema, json) {
  if (!json) { return Slice.empty }
  return new Slice(Fragment.fromJSON(schema, json.content), json.openLeft, json.openRight)
};

// :: (Fragment) → Slice
// Create a slice from a fragment by taking the maximum possible
// open value on both side of the fragment.
Slice.maxOpen = function (fragment) {
  var openLeft = 0, openRight = 0
  for (var n = fragment.firstChild; n && !n.isLeaf; n = n.firstChild) { openLeft++ }
  for (var n$1 = fragment.lastChild; n$1 && !n$1.isLeaf; n$1 = n$1.lastChild) { openRight++ }
  return new Slice(fragment, openLeft, openRight)
};

Object.defineProperties( Slice.prototype, prototypeAccessors$1 );
exports.Slice = Slice

function removeRange(content, from, to) {
  var ref = content.findIndex(from);
  var index = ref.index;
  var offset = ref.offset;
  var child = content.maybeChild(index)
  var ref$1 = content.findIndex(to);
  var indexTo = ref$1.index;
  var offsetTo = ref$1.offset;
  if (offset == from || child.isText) {
    if (offsetTo != to && !content.child(indexTo).isText) { throw new RangeError("Removing non-flat range") }
    return content.cut(0, from).append(content.cut(to))
  }
  if (index != indexTo) { throw new RangeError("Removing non-flat range") }
  return content.replaceChild(index, child.copy(removeRange(child.content, from - offset - 1, to - offset - 1)))
}

function insertInto(content, dist, insert, parent) {
  var ref = content.findIndex(dist);
  var index = ref.index;
  var offset = ref.offset;
  var child = content.maybeChild(index)
  if (offset == dist || child.isText) {
    if (parent && !parent.canReplace(index, index, insert)) { return null }
    return content.cut(0, dist).append(insert).append(content.cut(dist))
  }
  var inner = insertInto(child.content, dist - offset - 1, insert)
  return inner && content.replaceChild(index, child.copy(inner))
}

// :: Slice
// The empty slice.
Slice.empty = new Slice(Fragment.empty, 0, 0)

function replace($from, $to, slice) {
  if (slice.openLeft > $from.depth)
    { throw new ReplaceError("Inserted content deeper than insertion position") }
  if ($from.depth - slice.openLeft != $to.depth - slice.openRight)
    { throw new ReplaceError("Inconsistent open depths") }
  return replaceOuter($from, $to, slice, 0)
}
exports.replace = replace

function replaceOuter($from, $to, slice, depth) {
  var index = $from.index(depth), node = $from.node(depth)
  if (index == $to.index(depth) && depth < $from.depth - slice.openLeft) {
    var inner = replaceOuter($from, $to, slice, depth + 1)
    return node.copy(node.content.replaceChild(index, inner))
  } else if (!slice.content.size) {
    return close(node, replaceTwoWay($from, $to, depth))
  } else if (!slice.openLeft && !slice.openRight && $from.depth == depth && $to.depth == depth) { // Simple, flat case
    var parent = $from.parent, content = parent.content
    return close(parent, content.cut(0, $from.parentOffset).append(slice.content).append(content.cut($to.parentOffset)))
  } else {
    var ref = prepareSliceForReplace(slice, $from);
    var start = ref.start;
    var end = ref.end;
    return close(node, replaceThreeWay($from, start, end, $to, depth))
  }
}

function checkJoin(main, sub) {
  if (!sub.type.compatibleContent(main.type))
    { throw new ReplaceError("Cannot join " + sub.type.name + " onto " + main.type.name) }
}

function joinable($before, $after, depth) {
  var node = $before.node(depth)
  checkJoin(node, $after.node(depth))
  return node
}

function addNode(child, target) {
  var last = target.length - 1
  if (last >= 0 && child.isText && child.sameMarkup(target[last]))
    { target[last] = child.withText(target[last].text + child.text) }
  else
    { target.push(child) }
}

function addRange($start, $end, depth, target) {
  var node = ($end || $start).node(depth)
  var startIndex = 0, endIndex = $end ? $end.index(depth) : node.childCount
  if ($start) {
    startIndex = $start.index(depth)
    if ($start.depth > depth) {
      startIndex++
    } else if ($start.textOffset) {
      addNode($start.nodeAfter, target)
      startIndex++
    }
  }
  for (var i = startIndex; i < endIndex; i++) { addNode(node.child(i), target) }
  if ($end && $end.depth == depth && $end.textOffset)
    { addNode($end.nodeBefore, target) }
}

function close(node, content) {
  if (!node.type.validContent(content, node.attrs))
    { throw new ReplaceError("Invalid content for node " + node.type.name) }
  return node.copy(content)
}

function replaceThreeWay($from, $start, $end, $to, depth) {
  var openLeft = $from.depth > depth && joinable($from, $start, depth + 1)
  var openRight = $to.depth > depth && joinable($end, $to, depth + 1)

  var content = []
  addRange(null, $from, depth, content)
  if (openLeft && openRight && $start.index(depth) == $end.index(depth)) {
    checkJoin(openLeft, openRight)
    addNode(close(openLeft, replaceThreeWay($from, $start, $end, $to, depth + 1)), content)
  } else {
    if (openLeft)
      { addNode(close(openLeft, replaceTwoWay($from, $start, depth + 1)), content) }
    addRange($start, $end, depth, content)
    if (openRight)
      { addNode(close(openRight, replaceTwoWay($end, $to, depth + 1)), content) }
  }
  addRange($to, null, depth, content)
  return new Fragment(content)
}

function replaceTwoWay($from, $to, depth) {
  var content = []
  addRange(null, $from, depth, content)
  if ($from.depth > depth) {
    var type = joinable($from, $to, depth + 1)
    addNode(close(type, replaceTwoWay($from, $to, depth + 1)), content)
  }
  addRange($to, null, depth, content)
  return new Fragment(content)
}

function prepareSliceForReplace(slice, $along) {
  var extra = $along.depth - slice.openLeft, parent = $along.node(extra)
  var node = parent.copy(slice.content)
  for (var i = extra - 1; i >= 0; i--)
    { node = $along.node(i).copy(Fragment.from(node)) }
  return {start: node.resolveNoCache(slice.openLeft + extra),
          end: node.resolveNoCache(node.content.size - slice.openRight - extra)}
}


/***/ }),
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var ReplaceError = ref.ReplaceError;

var ref$1 = __webpack_require__(7);
var StepMap = ref$1.StepMap;

function mustOverride() { throw new Error("Override me") }

var stepsByID = Object.create(null)

// ::- A step object wraps an atomic operation. It generally applies
// only to the document it was created for, since the positions
// associated with it will only make sense for that document.
//
// New steps are defined by creating classes that extend `Step`,
// overriding the `apply`, `invert`, `map`, `getMap` and `fromJSON`
// methods, and registering your class with a unique
// JSON-serialization identifier using
// [`Step.jsonID`](#transform.Step^jsonID).
var Step = function Step () {};

Step.prototype.apply = function apply (_doc) { return mustOverride() };

// :: () → StepMap
// Get the step map that represents the changes made by this
// step.
Step.prototype.getMap = function getMap () { return StepMap.empty };

// :: (doc: Node) → Step
// Create an inverted version of this step. Needs the document as it
// was before the step as argument.
Step.prototype.invert = function invert (_doc) { return mustOverride() };

// :: (mapping: Mappable) → ?Step
// Map this step through a mappable thing, returning either a
// version of that step with its positions adjusted, or `null` if
// the step was entirely deleted by the mapping.
Step.prototype.map = function map (_mapping) { return mustOverride() };

// :: (other: Step) → ?Step
// Try to merge this step with another one, to be applied directly
// after it. Returns the merged step when possible, null if the
// steps can't be merged.
Step.prototype.merge = function merge (_other) { return null };

// :: (n: number) → Step
// Returns a copy of this step in which all positions have `n` added
// to them. The main use for this is to take a step in one document,
// and make it apply to a sub-document, or a larger document that
// the original document is a part of.
Step.prototype.offset = function offset (_n) { return mustOverride() };

// :: () → Object
// Create a JSON-serializeable representation of this step. By
// default, it'll create an object with the step's [JSON
// id](#transform.Step^jsonID), and each of the steps's own properties,
// automatically calling `toJSON` on the property values that have
// such a method.
Step.prototype.toJSON = function toJSON () {
    var this$1 = this;

  var obj = {stepType: this.jsonID}
  for (var prop in this$1) { if (this$1.hasOwnProperty(prop)) {
    var val = this$1[prop]
    obj[prop] = val && val.toJSON ? val.toJSON() : val
  } }
  return obj
};

// :: (Schema, Object) → Step
// Deserialize a step from its JSON representation. Will call
// through to the step class' own implementation of this method.
Step.fromJSON = function fromJSON (schema, json) {
  return stepsByID[json.stepType].fromJSON(schema, json)
};

// :: (string, constructor<Step>)
// To be able to serialize steps to JSON, each step needs a string
// ID to attach to its JSON representation. Use this method to
// register an ID for your step classes. Try to pick something
// that's unlikely to clash with steps from other modules.
Step.jsonID = function jsonID (id, stepClass) {
  if (id in stepsByID) { throw new RangeError("Duplicate use of step JSON ID " + id) }
  stepsByID[id] = stepClass
  stepClass.prototype.jsonID = id
  return stepClass
};
exports.Step = Step

// ::- The result of [applying](#transform.Step.apply) a step. Contains either a
// new document or a failure value.
var StepResult = function StepResult(doc, failed) {
  // :: ?Node The transformed document.
  this.doc = doc
  // :: ?string Text providing information about a failed step.
  this.failed = failed
};

// :: (Node) → StepResult
// Create a successful step result.
StepResult.ok = function ok (doc) { return new StepResult(doc, null) };

// :: (string) → StepResult
// Create a failed step result.
StepResult.fail = function fail (message) { return new StepResult(null, message) };

// :: (Node, number, number, Slice) → StepResult
// Call [`Node.replace`](#model.Node.replace) with the given
// arguments. Create a successful result if it succeeds, and a
// failed one if it throws a `ReplaceError`.
StepResult.fromReplace = function fromReplace (doc, from, to, slice) {
  try {
    return StepResult.ok(doc.replace(from, to, slice))
  } catch (e) {
    if (e instanceof ReplaceError) { return StepResult.fail(e.message) }
    throw e
  }
};
exports.StepResult = StepResult


/***/ }),
/* 13 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(1);
var TextSelection = ref.TextSelection;
var NodeSelection = ref.NodeSelection;

var browser = __webpack_require__(5)
var ref$1 = __webpack_require__(6);
var selectionCollapsed = ref$1.selectionCollapsed;

// Track the state of the current editor selection. Keeps the editor
// selection in sync with the DOM selection by polling for changes,
// as there is no DOM event for DOM selection changes.
var SelectionReader = function(view) {
  var this$1 = this;

  this.view = view

  // Track the state of the DOM selection.
  this.lastAnchorNode = this.lastHeadNode = this.lastAnchorOffset = this.lastHeadOffset = null
  this.lastSelection = view.state.selection
  this.ignoreUpdates = false
  this.poller = poller(this)

  view.dom.addEventListener("focus", function () { return this$1.poller.start(); })
  view.dom.addEventListener("blur", function () { return this$1.poller.stop(); })

  if (!view.editable) { this.poller.start() }
};

SelectionReader.prototype.destroy = function () { this.poller.stop() };

SelectionReader.prototype.poll = function (origin) { this.poller.poll(origin) };

SelectionReader.prototype.editableChanged = function () {
  if (!this.view.editable) { this.poller.start() }
  else if (!hasFocusAndSelection(this.view)) { this.poller.stop() }
};

// : () → bool
// Whether the DOM selection has changed from the last known state.
SelectionReader.prototype.domChanged = function () {
  var sel = this.view.root.getSelection()
  return sel.anchorNode != this.lastAnchorNode || sel.anchorOffset != this.lastAnchorOffset ||
    sel.focusNode != this.lastHeadNode || sel.focusOffset != this.lastHeadOffset
};

// Store the current state of the DOM selection.
SelectionReader.prototype.storeDOMState = function (selection) {
  var sel = this.view.root.getSelection()
  this.lastAnchorNode = sel.anchorNode; this.lastAnchorOffset = sel.anchorOffset
  this.lastHeadNode = sel.focusNode; this.lastHeadOffset = sel.focusOffset
  this.lastSelection = selection
};

SelectionReader.prototype.clearDOMState = function () {
  this.lastAnchorNode = this.lastSelection = null
};

// : (?string) → bool
// When the DOM selection changes in a notable manner, modify the
// current selection state to match.
SelectionReader.prototype.readFromDOM = function (origin) {
  if (this.ignoreUpdates || !this.domChanged() || !hasFocusAndSelection(this.view)) { return }
  if (!this.view.inDOMChange) { this.view.domObserver.flush() }
  if (this.view.inDOMChange) { return }

  var domSel = this.view.root.getSelection(), doc = this.view.state.doc
  var nearestDesc = this.view.docView.nearestDesc(domSel.focusNode)
  // If the selection is in a non-document part of the view, ignore it
  if (!nearestDesc.size) {
    this.storeDOMState()
    return
  }
  var head = this.view.docView.posFromDOM(domSel.focusNode, domSel.focusOffset)
  var $head = doc.resolve(head), $anchor, selection
  if (selectionCollapsed(domSel)) {
    $anchor = $head
    while (nearestDesc && !nearestDesc.node) { nearestDesc = nearestDesc.parent }
    if (nearestDesc && nearestDesc.node.isAtom && NodeSelection.isSelectable(nearestDesc.node)) {
      var pos = nearestDesc.posAtStart
      selection = new NodeSelection(head == pos ? $head : doc.resolve(pos))
    }
  } else {
    $anchor = doc.resolve(this.view.docView.posFromDOM(domSel.anchorNode, domSel.anchorOffset))
  }

  if (!selection) {
    var bias = origin == "pointer" || this.view.state.selection.head < $head.pos ? 1 : -1
    selection = selectionBetween(this.view, $anchor, $head, bias)
  }
  if (head == selection.head && $anchor.pos == selection.anchor)
    { this.storeDOMState(selection) }
  if (!this.view.state.selection.eq(selection)) {
    var tr = this.view.state.tr.setSelection(selection)
    if (origin == "pointer") { tr.setMeta("pointer", true) }
    this.view.dispatch(tr)
  }
};
exports.SelectionReader = SelectionReader

// There's two polling models. On browsers that support the
// selectionchange event (everything except Firefox, basically), we
// register a listener for that whenever the editor is focused.
var SelectionChangePoller = function(reader) {
  var this$1 = this;

  this.listening = false
  this.curOrigin = null
  this.originTime = 0
  this.reader = reader

  this.readFunc = function () { return reader.readFromDOM(this$1.originTime > Date.now() - 50 ? this$1.curOrigin : null); }
};

SelectionChangePoller.prototype.poll = function (origin) {
  this.curOrigin = origin
  this.originTime = Date.now()
};

SelectionChangePoller.prototype.start = function () {
  if (!this.listening) {
    document.addEventListener("selectionchange", this.readFunc)
    this.listening = true
    if (hasFocusAndSelection(this.reader.view)) { this.readFunc() }
  }
};

SelectionChangePoller.prototype.stop = function () {
  if (this.listening) {
    document.removeEventListener("selectionchange", this.readFunc)
    this.listening = false
  }
};

// On Firefox, we use timeout-based polling.
var TimeoutPoller = function(reader) {
  // The timeout ID for the poller when active.
  this.polling = null
  this.reader = reader
  this.pollFunc = this.doPoll.bind(this, null)
};

TimeoutPoller.prototype.doPoll = function (origin) {
  var view = this.reader.view
  if (view.focused || !view.editable) {
    this.reader.readFromDOM(origin)
    this.polling = setTimeout(this.pollFunc, 100)
  } else {
    this.polling = null
  }
};

TimeoutPoller.prototype.poll = function (origin) {
  clearTimeout(this.polling)
  this.polling = setTimeout(origin ? this.doPoll.bind(this, origin) : this.pollFunc, 0)
};

TimeoutPoller.prototype.start = function () {
  if (this.polling == null) { this.poll() }
};

TimeoutPoller.prototype.stop = function () {
  clearTimeout(this.polling)
  this.polling = null
};

function poller(reader) {
  return new ("onselectionchange" in document ? SelectionChangePoller : TimeoutPoller)(reader)
}

function selectionToDOM(view, takeFocus) {
  var sel = view.state.selection
  syncNodeSelection(view, sel)

  if (!view.hasFocus()) {
    if (!takeFocus) { return }
    // See https://bugzilla.mozilla.org/show_bug.cgi?id=921444
    if (browser.gecko && view.editable) {
      view.selectionReader.ignoreUpdates = true
      view.dom.focus()
      view.selectionReader.ignoreUpdates = false
    }
  }

  var reader = view.selectionReader
  if (reader.lastSelection && reader.lastSelection.eq(sel) && !reader.domChanged()) { return }

  reader.ignoreUpdates = true

  if (view.cursorWrapper) {
    selectCursorWrapper(view)
  } else {
    var anchor = sel.anchor;
    var head = sel.head;
    var resetEditableFrom, resetEditableTo
    if (browser.webkit && !(sel instanceof TextSelection)) {
      if (!sel.$from.parent.inlineContent)
        { resetEditableFrom = temporarilyEditableNear(view, sel.from) }
      if (!sel.empty && !sel.$from.parent.inlineContent)
        { resetEditableTo = temporarilyEditableNear(view, sel.to) }
    }
    view.docView.setSelection(anchor, head, view.root)
    if (browser.webkit) {
      if (resetEditableFrom) { resetEditableFrom.contentEditable = "false" }
      if (resetEditableTo) { resetEditableTo.contentEditable = "false" }
    }
    if (sel.visible) {
      view.dom.classList.remove("ProseMirror-hideselection")
    } else {
      view.dom.classList.add("ProseMirror-hideselection")
      if ("onselectionchange" in document) { removeClassOnSelectionChange(view) }
    }
  }

  reader.storeDOMState(sel)
  reader.ignoreUpdates = false
}
exports.selectionToDOM = selectionToDOM

// Kludge to work around Webkit not allowing a selection to start/end
// between non-editable block nodes. We briefly make something
// editable, set the selection, then set it uneditable again.
function temporarilyEditableNear(view, pos) {
  var ref = view.docView.domFromPos(pos);
  var node = ref.node;
  var offset = ref.offset;
  var after = offset < node.childNodes.length ? node.childNodes[offset] : null
  var before = offset ? node.childNodes[offset - 1] : null
  if ((!after || after.contentEditable == "false") && (!before || before.contentEditable == "false")) {
    if (after) {
      after.contentEditable = "true"
      return after
    } else if (before) {
      before.contentEditable = "true"
      return before
    }
  }
}

function removeClassOnSelectionChange(view) {
  document.removeEventListener("selectionchange", view.hideSelectionGuard)
  var domSel = view.root.getSelection()
  var node = domSel.anchorNode, offset = domSel.anchorOffset
  document.addEventListener("selectionchange", view.hideSelectionGuard = function () {
    if (domSel.anchorNode != node || domSel.anchorOffset != offset) {
      document.removeEventListener("selectionchange", view.hideSelectionGuard)
      view.dom.classList.remove("ProseMirror-hideselection")
    }
  })
}

function selectCursorWrapper(view) {
  var domSel = view.root.getSelection(), range = document.createRange()
  var node = view.cursorWrapper.type.widget
  range.setEnd(node, node.childNodes.length)
  range.collapse(false)
  domSel.removeAllRanges()
  domSel.addRange(range)
}

function syncNodeSelection(view, sel) {
  if (sel instanceof NodeSelection) {
    var desc = view.docView.descAt(sel.from)
    if (desc != view.lastSelectedViewDesc) {
      clearNodeSelection(view)
      if (desc) { desc.selectNode() }
      view.lastSelectedViewDesc = desc
    }
  } else {
    clearNodeSelection(view)
  }
}

// Clear all DOM statefulness of the last node selection.
function clearNodeSelection(view) {
  if (view.lastSelectedViewDesc) {
    view.lastSelectedViewDesc.deselectNode()
    view.lastSelectedViewDesc = null
  }
}

function selectionBetween(view, $anchor, $head, bias) {
  return view.someProp("createSelectionBetween", function (f) { return f(view, $anchor, $head); })
    || TextSelection.between($anchor, $head, bias)
}
exports.selectionBetween = selectionBetween

function hasFocusAndSelection(view) {
  if (view.editable && view.root.activeElement != view.dom) { return false }
  var sel = view.root.getSelection()
  return sel.anchorNode && view.dom.contains(sel.anchorNode.nodeType == 3 ? sel.anchorNode.parentNode : sel.anchorNode)
}


/***/ }),
/* 14 */
/***/ (function(module, exports) {

function compareDeep(a, b) {
  if (a === b) { return true }
  if (!(a && typeof a == "object") ||
      !(b && typeof b == "object")) { return false }
  var array = Array.isArray(a)
  if (Array.isArray(b) != array) { return false }
  if (array) {
    if (a.length != b.length) { return false }
    for (var i = 0; i < a.length; i++) { if (!compareDeep(a[i], b[i])) { return false } }
  } else {
    for (var p in a) { if (!(p in b) || !compareDeep(a[p], b[p])) { return false } }
    for (var p$1 in b) { if (!(p$1 in a)) { return false } }
  }
  return true
}
exports.compareDeep = compareDeep


/***/ }),
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(3);
var Fragment = ref.Fragment;
var ref$1 = __webpack_require__(4);
var Mark = ref$1.Mark;

var ContentExpr = function(nodeType, elements, inlineContent) {
  this.nodeType = nodeType
  this.elements = elements
  this.inlineContent = inlineContent
};

var prototypeAccessors = { isLeaf: {} };

prototypeAccessors.isLeaf.get = function () {
  return this.elements.length == 0
};

// : (?Object) → ContentMatch
// The content match at the start of this expression.
ContentExpr.prototype.start = function (attrs) {
  return new ContentMatch(this, attrs, 0, 0)
};

// : (NodeType, ?Object, ?Object) → ?ContentMatch
// Try to find a match that matches the given node, anywhere in the
// expression. (Useful when synthesizing a match for a node that's
// open to the left.)
ContentExpr.prototype.atType = function (parentAttrs, type, attrs, marks) {
    var this$1 = this;
    if ( marks === void 0 ) marks = Mark.none;

  for (var i = 0; i < this.elements.length; i++)
    { if (this$1.elements[i].matchesType(type, attrs, marks, parentAttrs, this$1))
      { return new ContentMatch(this$1, parentAttrs, i, 0) } }
};

ContentExpr.prototype.matches = function (attrs, fragment, from, to) {
  return this.start(attrs).matchToEnd(fragment, from, to)
};

// Get a position in a known-valid fragment. If this is a simple
// (single-element) expression, we don't have to do any matching,
// and can simply skip to the position with count `index`.
ContentExpr.prototype.getMatchAt = function (attrs, fragment, index) {
    if ( index === void 0 ) index = fragment.childCount;

  if (this.elements.length == 1)
    { return new ContentMatch(this, attrs, 0, index) }
  else
    { return this.start(attrs).matchFragment(fragment, 0, index) }
};

ContentExpr.prototype.checkReplace = function (attrs, content, from, to, replacement, start, end) {
    var this$1 = this;
    if ( replacement === void 0 ) replacement = Fragment.empty;
    if ( start === void 0 ) start = 0;
    if ( end === void 0 ) end = replacement.childCount;

  // Check for simple case, where the expression only has a single element
  // (Optimization to avoid matching more than we need)
  if (this.elements.length == 1) {
    var elt = this.elements[0]
    if (!checkCount(elt, content.childCount - (to - from) + (end - start), attrs, this)) { return false }
    for (var i = start; i < end; i++) { if (!elt.matches(replacement.child(i), attrs, this$1)) { return false } }
    return true
  }

  var match = this.getMatchAt(attrs, content, from).matchFragment(replacement, start, end)
  return match ? match.matchToEnd(content, to) : false
};

ContentExpr.prototype.checkReplaceWith = function (attrs, content, from, to, type, typeAttrs, marks) {
  if (this.elements.length == 1) {
    var elt = this.elements[0]
    if (!checkCount(elt, content.childCount - (to - from) + 1, attrs, this)) { return false }
    return elt.matchesType(type, typeAttrs, marks, attrs, this)
  }

  var match = this.getMatchAt(attrs, content, from).matchType(type, typeAttrs, marks)
  return match ? match.matchToEnd(content, to) : false
};

ContentExpr.prototype.compatible = function (other) {
    var this$1 = this;

  for (var i = 0; i < this.elements.length; i++) {
    var elt = this$1.elements[i]
    for (var j = 0; j < other.elements.length; j++)
      { if (other.elements[j].compatible(elt)) { return true } }
  }
  return false
};

ContentExpr.prototype.generateContent = function (attrs) {
  return this.start(attrs).fillBefore(Fragment.empty, true)
};

ContentExpr.parse = function (nodeType, expr) {
    var this$1 = this;

  var elements = [], pos = 0, inline = null
  for (;;) {
    pos += /^\s*/.exec(expr.slice(pos))[0].length
    if (pos == expr.length) { break }

    var types = /^(?:(\w+)|\(\s*(\w+(?:\s*\|\s*\w+)*)\s*\))/.exec(expr.slice(pos))
    if (!types) { throw new SyntaxError("Invalid content expression '" + expr + "' at " + pos) }
    pos += types[0].length
    var attrs = /^\[([^\]]+)\]/.exec(expr.slice(pos))
    if (attrs) { pos += attrs[0].length }
    var marks = /^<(?:(_)|\s*(\w+(?:\s+\w+)*)\s*)>/.exec(expr.slice(pos))
    if (marks) { pos += marks[0].length }
    var repeat = /^(?:([+*?])|\{\s*(\d+|\.\w+)\s*(,\s*(\d+|\.\w+)?)?\s*\})/.exec(expr.slice(pos))
    if (repeat) { pos += repeat[0].length }

    var nodeTypes = expandTypes(nodeType.schema, types[1] ? [types[1]] : types[2].split(/\s*\|\s*/))
    for (var i = 0; i < nodeTypes.length; i++) {
      if (inline == null) { inline = nodeTypes[i].isInline }
      else if (inline != nodeTypes[i].isInline) { throw new SyntaxError("Mixing inline and block content in a single node") }
    }
    var attrSet = !attrs ? null : parseAttrs(nodeType, attrs[1])
    var markSet = !marks ? false : marks[1] ? true : this$1.gatherMarks(nodeType.schema, marks[2].split(/\s+/))
    var ref = parseRepeat(nodeType, repeat);
      var min = ref.min;
      var max = ref.max;
    if (min != 0 && (nodeTypes[0].hasRequiredAttrs(attrSet) || nodeTypes[0].isText))
      { throw new SyntaxError("Node type " + types[0] + " in type " + nodeType.name +
                            " is required, but has non-optional attributes") }
    var newElt = new ContentElement(nodeTypes, attrSet, markSet, min, max)
    for (var i$1 = elements.length - 1; i$1 >= 0; i$1--) {
      var prev = elements[i$1]
      if (prev.min != prev.max && prev.overlaps(newElt))
        { throw new SyntaxError("Possibly ambiguous overlapping adjacent content expressions in '" + expr + "'") }
      if (prev.min != 0) { break }
    }
    elements.push(newElt)
  }

  return new ContentExpr(nodeType, elements, !!inline)
};

ContentExpr.gatherMarks = function (schema, marks) {
  var found = []
  for (var i = 0; i < marks.length; i++) {
    var name = marks[i], mark = schema.marks[name], ok = mark
    if (mark) {
      found.push(mark)
    } else {
      for (var prop in schema.marks) {
        var mark$1 = schema.marks[prop]
        if (name == "_" || (mark$1.spec.group && mark$1.spec.group.split(" ").indexOf(name) > -1))
          { found.push(ok = mark$1) }
      }
    }
    if (!ok) { throw new SyntaxError("Unknown mark type: '" + marks[i] + "'") }
  }
  return found
};

Object.defineProperties( ContentExpr.prototype, prototypeAccessors );
exports.ContentExpr = ContentExpr

var ContentElement = function(nodeTypes, attrs, marks, min, max) {
  this.nodeTypes = nodeTypes
  this.attrs = attrs
  this.marks = marks
  this.min = min
  this.max = max
};

ContentElement.prototype.matchesType = function (type, attrs, marks, parentAttrs, parentExpr) {
    var this$1 = this;

  if (this.nodeTypes.indexOf(type) == -1) { return false }
  if (this.attrs) {
    if (!attrs) { return false }
    for (var prop in this$1.attrs)
      { if (attrs[prop] != resolveValue(this$1.attrs[prop], parentAttrs, parentExpr)) { return false } }
  }
  if (this.marks === true) { return true }
  if (this.marks === false) { return marks.length == 0 }
  for (var i = 0; i < marks.length; i++)
    { if (this$1.marks.indexOf(marks[i].type) == -1) { return false } }
  return true
};

ContentElement.prototype.matches = function (node, parentAttrs, parentExpr) {
  return this.matchesType(node.type, node.attrs, node.marks, parentAttrs, parentExpr)
};

ContentElement.prototype.compatible = function (other) {
    var this$1 = this;

  for (var i = 0; i < this.nodeTypes.length; i++)
    { if (other.nodeTypes.indexOf(this$1.nodeTypes[i]) != -1) { return true } }
  return false
};

ContentElement.prototype.constrainedAttrs = function (parentAttrs, expr) {
    var this$1 = this;

  if (!this.attrs) { return null }
  var attrs = Object.create(null)
  for (var prop in this$1.attrs)
    { attrs[prop] = resolveValue(this$1.attrs[prop], parentAttrs, expr) }
  return attrs
};

ContentElement.prototype.createFiller = function (parentAttrs, expr) {
  var type = this.nodeTypes[0], attrs = type.computeAttrs(this.constrainedAttrs(parentAttrs, expr))
  return type.create(attrs, type.contentExpr.generateContent(attrs))
};

ContentElement.prototype.defaultType = function () {
  var first = this.nodeTypes[0]
  if (!(first.hasRequiredAttrs() || first.isText)) { return first }
};

ContentElement.prototype.overlaps = function (other) {
  return this.nodeTypes.some(function (t) { return other.nodeTypes.indexOf(t) > -1; })
};

ContentElement.prototype.allowsMark = function (markType) {
  return this.marks === true || this.marks && this.marks.indexOf(markType) > -1
};

// ::- Represents a partial match of a node type's [content
// expression](#model.NodeSpec), and can be used to find out whether further
// content matches here, and whether a given position is a valid end
// of the parent node.
var ContentMatch = function(expr, attrs, index, count) {
  this.expr = expr
  this.attrs = attrs
  this.index = index
  this.count = count
};

var prototypeAccessors$1 = { element: {},nextElement: {} };

prototypeAccessors$1.element.get = function () { return this.expr.elements[this.index] };

prototypeAccessors$1.nextElement.get = function () {
    var this$1 = this;

  for (var i = this.index, count = this.count; i < this.expr.elements.length; i++) {
    var element = this$1.expr.elements[i]
    if (this$1.resolveValue(element.max) > count) { return element }
    count = 0
  }
};

ContentMatch.prototype.move = function (index, count) {
  return new ContentMatch(this.expr, this.attrs, index, count)
};

ContentMatch.prototype.resolveValue = function (value) {
  return value instanceof AttrValue ? resolveValue(value, this.attrs, this.expr) : value
};

// :: (Node) → ?ContentMatch
// Match a node, returning a new match after the node if successful.
ContentMatch.prototype.matchNode = function (node) {
  return this.matchType(node.type, node.attrs, node.marks)
};

// :: (NodeType, ?Object, [Mark]) → ?ContentMatch
// Match a node type and marks, returning an match after that node
// if successful.
ContentMatch.prototype.matchType = function (type, attrs, marks) {
    var this$1 = this;
    if ( marks === void 0 ) marks = Mark.none;

  for (var ref = this, index = ref.index, count = ref.count; index < this.expr.elements.length; index++, count = 0) {
    var elt = this$1.expr.elements[index], max = this$1.resolveValue(elt.max)
    if (count < max && elt.matchesType(type, attrs, marks, this$1.attrs, this$1.expr)) {
      count++
      return this$1.move(index, count)
    }
    if (count < this$1.resolveValue(elt.min)) { return null }
  }
};

// :: (Fragment, ?number, ?number) → ?union<ContentMatch, bool>
// Try to match a fragment. Returns a new match when successful,
// `null` when it ran into a required element it couldn't fit, and
// `false` if it reached the end of the expression without
// matching all nodes.
ContentMatch.prototype.matchFragment = function (fragment, from, to) {
    var this$1 = this;
    if ( from === void 0 ) from = 0;
    if ( to === void 0 ) to = fragment.childCount;

  if (from == to) { return this }
  var fragPos = from, end = this.expr.elements.length
  for (var ref = this, index = ref.index, count = ref.count; index < end; index++, count = 0) {
    var elt = this$1.expr.elements[index], max = this$1.resolveValue(elt.max)

    while (count < max && fragPos < to) {
      if (elt.matches(fragment.child(fragPos), this$1.attrs, this$1.expr)) {
        count++
        if (++fragPos == to) { return this$1.move(index, count) }
      } else {
        break
      }
    }
    if (count < this$1.resolveValue(elt.min)) { return null }
  }
  return false
};

// :: (Fragment, ?number, ?number) → bool
// Returns true only if the fragment matches here, and reaches all
// the way to the end of the content expression.
ContentMatch.prototype.matchToEnd = function (fragment, start, end) {
  var matched = this.matchFragment(fragment, start, end)
  return matched && matched.validEnd() || false
};

// :: () → bool
// Returns true if this position represents a valid end of the
// expression (no required content follows after it).
ContentMatch.prototype.validEnd = function () {
    var this$1 = this;

  for (var i = this.index, count = this.count; i < this.expr.elements.length; i++, count = 0)
    { if (count < this$1.resolveValue(this$1.expr.elements[i].min)) { return false } }
  return true
};

// :: (Fragment, bool, ?number) → ?Fragment
// Try to match the given fragment, and if that fails, see if it can
// be made to match by inserting nodes in front of it. When
// successful, return a fragment of inserted nodes (which may be
// empty if nothing had to be inserted). When `toEnd` is true, only
// return a fragment if the resulting match goes to the end of the
// content expression.
ContentMatch.prototype.fillBefore = function (after, toEnd, startIndex) {
    var this$1 = this;

  var added = [], match = this, index = startIndex || 0, end = this.expr.elements.length
  for (;;) {
    var fits = match.matchFragment(after, index)
    if (fits && (!toEnd || fits.validEnd())) { return Fragment.from(added) }
    if (fits === false) { return null } // Matched to end with content remaining

    var elt = match.element
    if (match.count < this$1.resolveValue(elt.min)) {
      added.push(elt.createFiller(this$1.attrs, this$1.expr))
      match = match.move(match.index, match.count + 1)
    } else if (match.index < end) {
      match = match.move(match.index + 1, 0)
    } else if (after.childCount > index) {
      return null
    } else {
      return Fragment.from(added)
    }
  }
};

ContentMatch.prototype.possibleContent = function () {
    var this$1 = this;

  var found = []
  for (var i = this.index, count = this.count; i < this.expr.elements.length; i++, count = 0) {
    var elt = this$1.expr.elements[i], attrs = elt.constrainedAttrs(this$1.attrs, this$1.expr)
    if (count < this$1.resolveValue(elt.max)) { for (var j = 0; j < elt.nodeTypes.length; j++) {
      var type = elt.nodeTypes[j]
      if (!type.hasRequiredAttrs(attrs) && !type.isText) { found.push({type: type, attrs: attrs}) }
    } }
    if (this$1.resolveValue(elt.min) > count) { break }
  }
  return found
};

// :: (MarkType) → bool
// Check whether a node with the given mark type is allowed after
// this position.
ContentMatch.prototype.allowsMark = function (markType) {
  return this.element.allowsMark(markType)
};

// :: (NodeType, ?Object, ?[Mark]) → ?[{type: NodeType, attrs: Object}]
// Find a set of wrapping node types that would allow a node of type
// `target` with attributes `targetAttrs` to appear at this
// position. The result may be empty (when it fits directly) and
// will be null when no such wrapping exists.
ContentMatch.prototype.findWrapping = function (target, targetAttrs, targetMarks) {
  var seen = Object.create(null), first = {match: this, via: null}, active = [first]
  while (active.length) {
    var current = active.shift(), match = current.match
    if (match.matchType(target, targetAttrs, targetMarks)) {
      var result = []
      for (var obj = current; obj != first; obj = obj.via)
        { result.push({type: obj.match.expr.nodeType, attrs: obj.match.attrs}) }
      return result.reverse()
    }
    var possible = match.possibleContent()
    for (var i = 0; i < possible.length; i++) {
      var ref = possible[i];
        var type = ref.type;
        var attrs = ref.attrs;
        var fullAttrs = type.computeAttrs(attrs)
      if (!type.isLeaf && !(type.name in seen) &&
          (current == first || match.matchType(type, fullAttrs).validEnd())) {
        active.push({match: type.contentExpr.start(fullAttrs), via: current})
        seen[type.name] = true
      }
    }
  }
};

// :: (Node) → ?[{type: NodeType, attrs: Object}]
// Call [`findWrapping`](#model.ContentMatch.findWrapping) with the
// properties of the given node.
ContentMatch.prototype.findWrappingFor = function (node) {
  return this.findWrapping(node.type, node.attrs, node.marks)
};

Object.defineProperties( ContentMatch.prototype, prototypeAccessors$1 );
exports.ContentMatch = ContentMatch

var AttrValue = function(attr) { this.attr = attr };

function parseValue(nodeType, value) {
  if (value.charAt(0) == ".") {
    var attr = value.slice(1)
    if (!nodeType.attrs[attr]) { throw new SyntaxError("Node type " + nodeType.name + " has no attribute " + attr) }
    return new AttrValue(attr)
  } else {
    return JSON.parse(value)
  }
}

function resolveValue(value, attrs, expr) {
  if (!(value instanceof AttrValue)) { return value }
  var attrVal = attrs && attrs[value.attr]
  return attrVal !== undefined ? attrVal : expr.nodeType.defaultAttrs[value.attr]
}

function checkCount(elt, count, attrs, expr) {
  return count >= resolveValue(elt.min, attrs, expr) &&
    count <= resolveValue(elt.max, attrs, expr)
}

function expandTypes(schema, types) {
  var result = []
  types.forEach(function (type) {
    var found = schema.nodes[type]
    if (found) {
      if (result.indexOf(found) == -1) { result.push(found) }
    } else {
      for (var name in schema.nodes) {
        var nodeType = schema.nodes[name]
        if (nodeType.groups.indexOf(type) > -1 && result.indexOf(nodeType) == -1)
          { found = result.push(nodeType) }
      }
    }
    if (!found)
      { throw new SyntaxError("Node type or group '" + type + "' does not exist") }
  })
  return result
}

var many = 2e9 // Big number representable as a 32-bit int

function parseRepeat(nodeType, match) {
  var min = 1, max = 1
  if (match) {
    if (match[1] == "+") {
      max = many
    } else if (match[1] == "*") {
      min = 0
      max = many
    } else if (match[1] == "?") {
      min = 0
    } else if (match[2]) {
      min = parseValue(nodeType, match[2])
      if (match[3])
        { max = match[4] ? parseValue(nodeType, match[4]) : many }
      else
        { max = min }
    }
    if (max == 0 || min > max)
      { throw new SyntaxError("Invalid repeat count in '" + match[0] + "'") }
  }
  return {min: min, max: max}
}

function parseAttrs(nodeType, expr) {
  var parts = expr.split(/\s*,\s*/)
  var attrs = Object.create(null)
  for (var i = 0; i < parts.length; i++) {
    var match = /^(\w+)=(\w+|\"(?:\\.|[^\\])*\"|\.\w+)$/.exec(parts[i])
    if (!match) { throw new SyntaxError("Invalid attribute syntax: " + parts[i]) }
    attrs[match[1]] = parseValue(nodeType, match[2])
  }
  return attrs
}


/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(3);
var Fragment = ref.Fragment;
var ref$1 = __webpack_require__(4);
var Mark = ref$1.Mark;
var ref$2 = __webpack_require__(11);
var Slice = ref$2.Slice;
var replace = ref$2.replace;
var ref$3 = __webpack_require__(17);
var ResolvedPos = ref$3.ResolvedPos;
var ref$4 = __webpack_require__(14);
var compareDeep = ref$4.compareDeep;

var emptyAttrs = Object.create(null)

// ::- This class represents a node in the tree that makes up a
// ProseMirror document. So a document is an instance of `Node`, with
// children that are also instances of `Node`.
//
// Nodes are persistent data structures. Instead of changing them, you
// create new ones with the content you want. Old ones keep pointing
// at the old document shape. This is made cheaper by sharing
// structure between the old and new data as much as possible, which a
// tree shape like this (without back pointers) makes easy.
//
// **Never** directly mutate the properties of a `Node` object. See
// [this guide](/docs/guides/doc/) for more information.
var Node = function(type, attrs, content, marks) {
  // :: NodeType
  // The type of node that this is.
  this.type = type

  // :: Object
  // An object mapping attribute names to values. The kind of
  // attributes allowed and required are determined by the node
  // type.
  this.attrs = attrs

  // :: Fragment
  // A container holding the node's children.
  this.content = content || Fragment.empty

  // :: [Mark]
  // The marks (things like whether it is emphasized or part of a
  // link) associated with this node.
  this.marks = marks || Mark.none
};

var prototypeAccessors = { nodeSize: {},childCount: {},textContent: {},firstChild: {},lastChild: {},isBlock: {},isTextblock: {},inlineContent: {},isInline: {},isText: {},isLeaf: {},isAtom: {} };

// text:: ?string
// For text nodes, this contains the node's text content.

// :: number
// The size of this node, as defined by the integer-based [indexing
// scheme](/docs/guides/doc/#indexing). For text nodes, this is the
// amount of characters. For other leaf nodes, it is one. And for
// non-leaf nodes, it is the size of the content plus two (the start
// and end token).
prototypeAccessors.nodeSize.get = function () { return this.isLeaf ? 1 : 2 + this.content.size };

// :: number
// The number of children that the node has.
prototypeAccessors.childCount.get = function () { return this.content.childCount };

// :: (number) → Node
// Get the child node at the given index. Raises an error when the
// index is out of range.
Node.prototype.child = function (index) { return this.content.child(index) };

// :: (number) → ?Node
// Get the child node at the given index, if it exists.
Node.prototype.maybeChild = function (index) { return this.content.maybeChild(index) };

// :: ((node: Node, offset: number, index: number))
// Call `f` for every child node, passing the node, its offset
// into this parent node, and its index.
Node.prototype.forEach = function (f) { this.content.forEach(f) };

// :: (?number, ?number, (node: Node, pos: number, parent: Node, index: number))
// Invoke a callback for all descendant nodes recursively between
// the given two positions that are relative to start of this node's content.
// The callback is invoked with the node, its parent-relative position,
// its parent node, and its child index. If the callback returns false,
// the current node's children will not be recursed over.
Node.prototype.nodesBetween = function (from, to, f, pos) {
    if ( pos === void 0 ) pos = 0;

  this.content.nodesBetween(from, to, f, pos, this)
};

// :: ((node: Node, pos: number, parent: Node))
// Call the given callback for every descendant node.
Node.prototype.descendants = function (f) {
  this.nodesBetween(0, this.content.size, f)
};

// :: string
// Concatenates all the text nodes found in this fragment and its
// children.
prototypeAccessors.textContent.get = function () { return this.textBetween(0, this.content.size, "") };

// :: (number, number, ?string, ?string) → string
// Get all text between positions `from` and `to`. When
// `blockSeparator` is given, it will be inserted whenever a new
// block node is started. When `leafText` is given, it'll be
// inserted for every non-text leaf node encountered.
Node.prototype.textBetween = function (from, to, blockSeparator, leafText) {
  return this.content.textBetween(from, to, blockSeparator, leafText)
};

// :: ?Node
// Returns this node's first child, or `null` if there are no
// children.
prototypeAccessors.firstChild.get = function () { return this.content.firstChild };

// :: ?Node
// Returns this node's last child, or `null` if there are no
// children.
prototypeAccessors.lastChild.get = function () { return this.content.lastChild };

// :: (Node) → bool
// Test whether two nodes represent the same content.
Node.prototype.eq = function (other) {
  return this == other || (this.sameMarkup(other) && this.content.eq(other.content))
};

// :: (Node) → bool
// Compare the markup (type, attributes, and marks) of this node to
// those of another. Returns `true` if both have the same markup.
Node.prototype.sameMarkup = function (other) {
  return this.hasMarkup(other.type, other.attrs, other.marks)
};

// :: (NodeType, ?Object, ?[Mark]) → bool
// Check whether this node's markup correspond to the given type,
// attributes, and marks.
Node.prototype.hasMarkup = function (type, attrs, marks) {
  return this.type == type &&
    compareDeep(this.attrs, attrs || type.defaultAttrs || emptyAttrs) &&
    Mark.sameSet(this.marks, marks || Mark.none)
};

// :: (?Fragment) → Node
// Create a new node with the same markup as this node, containing
// the given content (or empty, if no content is given).
Node.prototype.copy = function (content) {
    if ( content === void 0 ) content = null;

  if (content == this.content) { return this }
  return new this.constructor(this.type, this.attrs, content, this.marks)
};

// :: ([Mark]) → Node
// Create a copy of this node, with the given set of marks instead
// of the node's own marks.
Node.prototype.mark = function (marks) {
  return marks == this.marks ? this : new this.constructor(this.type, this.attrs, this.content, marks)
};

// :: (number, ?number) → Node
// Create a copy of this node with only the content between the
// given offsets. If `to` is not given, it defaults to the end of
// the node.
Node.prototype.cut = function (from, to) {
  if (from == 0 && to == this.content.size) { return this }
  return this.copy(this.content.cut(from, to))
};

// :: (number, ?number) → Slice
// Cut out the part of the document between the given positions, and
// return it as a `Slice` object.
Node.prototype.slice = function (from, to, includeParents) {
    if ( to === void 0 ) to = this.content.size;
    if ( includeParents === void 0 ) includeParents = false;

  if (from == to) { return Slice.empty }

  var $from = this.resolve(from), $to = this.resolve(to)
  var depth = includeParents ? 0 : $from.sharedDepth(to)
  var start = $from.start(depth), node = $from.node(depth)
  var content = node.content.cut($from.pos - start, $to.pos - start)
  return new Slice(content, $from.depth - depth, $to.depth - depth)
};

// :: (number, number, Slice) → Node
// Replace the part of the document between the given positions with
// the given slice. The slice must 'fit', meaning its open sides
// must be able to connect to the surrounding content, and its
// content nodes must be valid children for the node they are placed
// into. If any of this is violated, an error of type
// [`ReplaceError`](#model.ReplaceError) is thrown.
Node.prototype.replace = function (from, to, slice) {
  return replace(this.resolve(from), this.resolve(to), slice)
};

// :: (number) → ?Node
// Find the node after the given position.
Node.prototype.nodeAt = function (pos) {
  for (var node = this;;) {
    var ref = node.content.findIndex(pos);
      var index = ref.index;
      var offset = ref.offset;
    node = node.maybeChild(index)
    if (!node) { return null }
    if (offset == pos || node.isText) { return node }
    pos -= offset + 1
  }
};

// :: (number) → {node: ?Node, index: number, offset: number}
// Find the (direct) child node after the given offset, if any,
// and return it along with its index and offset relative to this
// node.
Node.prototype.childAfter = function (pos) {
  var ref = this.content.findIndex(pos);
    var index = ref.index;
    var offset = ref.offset;
  return {node: this.content.maybeChild(index), index: index, offset: offset}
};

// :: (number) → {node: ?Node, index: number, offset: number}
// Find the (direct) child node before the given offset, if any,
// and return it along with its index and offset relative to this
// node.
Node.prototype.childBefore = function (pos) {
  if (pos == 0) { return {node: null, index: 0, offset: 0} }
  var ref = this.content.findIndex(pos);
    var index = ref.index;
    var offset = ref.offset;
  if (offset < pos) { return {node: this.content.child(index), index: index, offset: offset} }
  var node = this.content.child(index - 1)
  return {node: node, index: index - 1, offset: offset - node.nodeSize}
};

// :: (number) → ResolvedPos
// Resolve the given position in the document, returning an object
// describing its path through the document.
Node.prototype.resolve = function (pos) { return ResolvedPos.resolveCached(this, pos) };

Node.prototype.resolveNoCache = function (pos) { return ResolvedPos.resolve(this, pos) };

// :: (?number, ?number, MarkType) → bool
// Test whether a mark of the given type occurs in this document
// between the two given positions.
Node.prototype.rangeHasMark = function (from, to, type) {
  var found = false
  this.nodesBetween(from, to, function (node) {
    if (type.isInSet(node.marks)) { found = true }
    return !found
  })
  return found
};

// :: bool
// True when this is a block (non-inline node)
prototypeAccessors.isBlock.get = function () { return this.type.isBlock };

// :: bool
// True when this is a textblock node, a block node with inline
// content.
prototypeAccessors.isTextblock.get = function () { return this.type.isTextblock };

// :: bool
// True when this node has inline content.
prototypeAccessors.inlineContent.get = function () { return this.type.inlineContent };

// :: bool
// True when this is an inline node (a text node or a node that can
// appear among text).
prototypeAccessors.isInline.get = function () { return this.type.isInline };

// :: bool
// True when this is a text node.
prototypeAccessors.isText.get = function () { return this.type.isText };

// :: bool
// True when this is a leaf node.
prototypeAccessors.isLeaf.get = function () { return this.type.isLeaf };

// :: bool
// True when this is an atom, i.e. when it does not have directly
// editable content. This is usually the same as `isLeaf`, but can
// be configured with the [`leaf` property](#model.NodeSpec.leaf) on
// a node's spec (typically when the node is displayed as an
// uneditable [node view](#view.NodeView)).
prototypeAccessors.isAtom.get = function () { return this.type.isAtom };

// :: () → string
// Return a string representation of this node for debugging
// purposes.
Node.prototype.toString = function () {
  var name = this.type.name
  if (this.content.size)
    { name += "(" + this.content.toStringInner() + ")" }
  return wrapMarks(this.marks, name)
};

// :: (number) → ContentMatch
// Get the content match in this node at the given index.
Node.prototype.contentMatchAt = function (index) {
  return this.type.contentExpr.getMatchAt(this.attrs, this.content, index)
};

// :: (number, number, ?Fragment, ?number, ?number) → bool
// Test whether replacing the range `from` to `to` (by index) with
// the given replacement fragment (which defaults to the empty
// fragment) would leave the node's content valid. You can
// optionally pass `start` and `end` indices into the replacement
// fragment.
Node.prototype.canReplace = function (from, to, replacement, start, end) {
  return this.type.contentExpr.checkReplace(this.attrs, this.content, from, to, replacement, start, end)
};

// :: (number, number, NodeType, ?[Mark]) → bool
// Test whether replacing the range `from` to `to` (by index) with a
// node of the given type with the given attributes and marks would
// be valid.
Node.prototype.canReplaceWith = function (from, to, type, attrs, marks) {
  return this.type.contentExpr.checkReplaceWith(this.attrs, this.content, from, to, type, attrs, marks || Mark.none)
};

// :: (Node) → bool
// Test whether the given node's content could be appended to this
// node. If that node is empty, this will only return true if there
// is at least one node type that can appear in both nodes (to avoid
// merging completely incompatible nodes).
Node.prototype.canAppend = function (other) {
  if (other.content.size) { return this.canReplace(this.childCount, this.childCount, other.content) }
  else { return this.type.compatibleContent(other.type) }
};

Node.prototype.defaultContentType = function (at) {
  var elt = this.contentMatchAt(at).nextElement
  return elt && elt.defaultType()
};

// :: ()
// Check whether this node and its descendants conform to the
// schema, and raise error when they do not.
Node.prototype.check = function () {
  if (!this.type.validContent(this.content, this.attrs))
    { throw new RangeError(("Invalid content for node " + (this.type.name) + ": " + (this.content.toString().slice(0, 50)))) }
  this.content.forEach(function (node) { return node.check(); })
};

// :: () → Object
// Return a JSON-serializeable representation of this node.
Node.prototype.toJSON = function () {
    var this$1 = this;

  var obj = {type: this.type.name}
  for (var _ in this$1.attrs) {
    obj.attrs = this$1.attrs
    break
  }
  if (this.content.size)
    { obj.content = this.content.toJSON() }
  if (this.marks.length)
    { obj.marks = this.marks.map(function (n) { return n.toJSON(); }) }
  return obj
};

// :: (Schema, Object) → Node
// Deserialize a node from its JSON representation.
Node.fromJSON = function (schema, json) {
  var marks = json.marks && json.marks.map(schema.markFromJSON)
  if (json.type == "text") { return schema.text(json.text, marks) }
  var type = schema.nodeType(json.type)
  if (!type) { throw new RangeError(("There is no node type " + (json.type) + " in this schema")) }
  return type.create(json.attrs, Fragment.fromJSON(schema, json.content), marks)
};

Object.defineProperties( Node.prototype, prototypeAccessors );
exports.Node = Node

var TextNode = (function (Node) {
  function TextNode(type, attrs, content, marks) {
    Node.call(this, type, attrs, null, marks)

    if (!content) { throw new RangeError("Empty text nodes are not allowed") }

    this.text = content
  }

  if ( Node ) TextNode.__proto__ = Node;
  TextNode.prototype = Object.create( Node && Node.prototype );
  TextNode.prototype.constructor = TextNode;

  var prototypeAccessors$1 = { textContent: {},nodeSize: {} };

  TextNode.prototype.toString = function () { return wrapMarks(this.marks, JSON.stringify(this.text)) };

  prototypeAccessors$1.textContent.get = function () { return this.text };

  TextNode.prototype.textBetween = function (from, to) { return this.text.slice(from, to) };

  prototypeAccessors$1.nodeSize.get = function () { return this.text.length };

  TextNode.prototype.mark = function (marks) {
    return new TextNode(this.type, this.attrs, this.text, marks)
  };

  TextNode.prototype.withText = function (text) {
    if (text == this.text) { return this }
    return new TextNode(this.type, this.attrs, text, this.marks)
  };

  TextNode.prototype.cut = function (from, to) {
    if ( from === void 0 ) from = 0;
    if ( to === void 0 ) to = this.text.length;

    if (from == 0 && to == this.text.length) { return this }
    return this.withText(this.text.slice(from, to))
  };

  TextNode.prototype.eq = function (other) {
    return this.sameMarkup(other) && this.text == other.text
  };

  TextNode.prototype.toJSON = function () {
    var base = Node.prototype.toJSON.call(this)
    base.text = this.text
    return base
  };

  Object.defineProperties( TextNode.prototype, prototypeAccessors$1 );

  return TextNode;
}(Node));
exports.TextNode = TextNode

function wrapMarks(marks, str) {
  for (var i = marks.length - 1; i >= 0; i--)
    { str = marks[i].type.name + "(" + str + ")" }
  return str
}


/***/ }),
/* 17 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(4);
var Mark = ref.Mark;

// ::- You'll often have to '[resolve](#model.Node.resolve)' a
// position to get the context you need. Objects of this class
// represent such a resolved position, providing various pieces of
// context information and helper methods.
//
// Throughout this interface, methods that take an optional `depth`
// parameter will interpret undefined as `this.depth` and negative
// numbers as `this.depth + value`.
var ResolvedPos = function(pos, path, parentOffset) {
  // :: number The position that was resolved.
  this.pos = pos
  this.path = path
  // :: number
  // The number of levels the parent node is from the root. If this
  // position points directly into the root, it is 0. If it points
  // into a top-level paragraph, 1, and so on.
  this.depth = path.length / 3 - 1
  // :: number The offset this position has into its parent node.
  this.parentOffset = parentOffset
};

var prototypeAccessors = { parent: {},textOffset: {},nodeAfter: {},nodeBefore: {} };

ResolvedPos.prototype.resolveDepth = function (val) {
  if (val == null) { return this.depth }
  if (val < 0) { return this.depth + val }
  return val
};

// :: Node
// The parent node that the position points into. Note that even if
// a position points into a text node, that node is not considered
// the parent—text nodes are 'flat' in this model.
prototypeAccessors.parent.get = function () { return this.node(this.depth) };

// :: (?number) → Node
// The ancestor node at the given level. `p.node(p.depth)` is the
// same as `p.parent`.
ResolvedPos.prototype.node = function (depth) { return this.path[this.resolveDepth(depth) * 3] };

// :: (?number) → number
// The index into the ancestor at the given level. If this points at
// the 3rd node in the 2nd paragraph on the top level, for example,
// `p.index(0)` is 2 and `p.index(1)` is 3.
ResolvedPos.prototype.index = function (depth) { return this.path[this.resolveDepth(depth) * 3 + 1] };

// :: (?number) → number
// The index pointing after this position into the ancestor at the
// given level.
ResolvedPos.prototype.indexAfter = function (depth) {
  depth = this.resolveDepth(depth)
  return this.index(depth) + (depth == this.depth && !this.textOffset ? 0 : 1)
};

// :: (?number) → number
// The (absolute) position at the start of the node at the given
// level.
ResolvedPos.prototype.start = function (depth) {
  depth = this.resolveDepth(depth)
  return depth == 0 ? 0 : this.path[depth * 3 - 1] + 1
};

// :: (?number) → number
// The (absolute) position at the end of the node at the given
// level.
ResolvedPos.prototype.end = function (depth) {
  depth = this.resolveDepth(depth)
  return this.start(depth) + this.node(depth).content.size
};

// :: (?number) → number
// The (absolute) position directly before the node at the given
// level, or, when `level` is `this.level + 1`, the original
// position.
ResolvedPos.prototype.before = function (depth) {
  depth = this.resolveDepth(depth)
  if (!depth) { throw new RangeError("There is no position before the top-level node") }
  return depth == this.depth + 1 ? this.pos : this.path[depth * 3 - 1]
};

// :: (?number) → number
// The (absolute) position directly after the node at the given
// level, or, when `level` is `this.level + 1`, the original
// position.
ResolvedPos.prototype.after = function (depth) {
  depth = this.resolveDepth(depth)
  if (!depth) { throw new RangeError("There is no position after the top-level node") }
  return depth == this.depth + 1 ? this.pos : this.path[depth * 3 - 1] + this.path[depth * 3].nodeSize
};

// :: number
// When this position points into a text node, this returns the
// distance between the position and the start of the text node.
// Will be zero for positions that point between nodes.
prototypeAccessors.textOffset.get = function () { return this.pos - this.path[this.path.length - 1] };

// :: ?Node
// Get the node directly after the position, if any. If the position
// points into a text node, only the part of that node after the
// position is returned.
prototypeAccessors.nodeAfter.get = function () {
  var parent = this.parent, index = this.index(this.depth)
  if (index == parent.childCount) { return null }
  var dOff = this.pos - this.path[this.path.length - 1], child = parent.child(index)
  return dOff ? parent.child(index).cut(dOff) : child
};

// :: ?Node
// Get the node directly before the position, if any. If the
// position points into a text node, only the part of that node
// before the position is returned.
prototypeAccessors.nodeBefore.get = function () {
  var index = this.index(this.depth)
  var dOff = this.pos - this.path[this.path.length - 1]
  if (dOff) { return this.parent.child(index).cut(0, dOff) }
  return index == 0 ? null : this.parent.child(index - 1)
};

// :: (?bool) → [Mark]
// Get the marks at this position, factoring in the surrounding
// marks' [`inclusive`](##model.MarkSpec.inclusive) property. If the
// position is at the start of a non-empty node, or `after` is true,
// the marks of the node after it (if any) are returned.
ResolvedPos.prototype.marks = function (after) {
  var parent = this.parent, index = this.index()

  // In an empty parent, return the empty array
  if (parent.content.size == 0) { return Mark.none }

  // When inside a text node, just return the text node's marks
  if (this.textOffset) { return parent.child(index).marks }

  var main = parent.maybeChild(index - 1), other = parent.maybeChild(index)
  // If the `after` flag is true of there is no node before, make
  // the node after this position the main reference.
  if ((after && other) || !main) { var tmp = main; main = other; other = tmp }

  // Use all marks in the main node, except those that have
  // `inclusive` set to false and are not present in the other node.
  var marks = main.marks
  for (var i = 0; i < marks.length; i++)
    { if (marks[i].type.spec.inclusive === false && (!other || !marks[i].isInSet(other.marks)))
      { marks = marks[i--].removeFromSet(marks) } }

  return marks
};

// :: (number) → number
// The depth up to which this position and the given (non-resolved)
// position share the same parent nodes.
ResolvedPos.prototype.sharedDepth = function (pos) {
    var this$1 = this;

  for (var depth = this.depth; depth > 0; depth--)
    { if (this$1.start(depth) <= pos && this$1.end(depth) >= pos) { return depth } }
  return 0
};

// :: (?ResolvedPos, ?(Node) → bool) → ?NodeRange
// Returns a range based on the place where this position and the
// given position diverge around block content. If both point into
// the same textblock, for example, a range around that textblock
// will be returned. If they point into different blocks, the range
// around those blocks or their ancestors in their common ancestor
// is returned. You can pass in an optional predicate that will be
// called with a parent node to see if a range into that parent is
// acceptable.
ResolvedPos.prototype.blockRange = function (other, pred) {
    var this$1 = this;
    if ( other === void 0 ) other = this;

  if (other.pos < this.pos) { return other.blockRange(this) }
  for (var d = this.depth - (this.parent.inlineContent || this.pos == other.pos ? 1 : 0); d >= 0; d--)
    { if (other.pos <= this$1.end(d) && (!pred || pred(this$1.node(d))))
      { return new NodeRange(this$1, other, d) } }
};

// :: (ResolvedPos) → bool
// Query whether the given position shares the same parent node.
ResolvedPos.prototype.sameParent = function (other) {
  return this.pos - this.parentOffset == other.pos - other.parentOffset
};

// :: (ResolvedPos) → ResolvedPos
// Return the greater of this and the given position.
ResolvedPos.prototype.max = function (other) {
  return other.pos > this.pos ? other : this
};

// :: (ResolvedPos) → ResolvedPos
// Return the smaller of this and the given position.
ResolvedPos.prototype.min = function (other) {
  return other.pos < this.pos ? other : this
};

ResolvedPos.prototype.toString = function () {
    var this$1 = this;

  var str = ""
  for (var i = 1; i <= this.depth; i++)
    { str += (str ? "/" : "") + this$1.node(i).type.name + "_" + this$1.index(i - 1) }
  return str + ":" + this.parentOffset
};

ResolvedPos.resolve = function (doc, pos) {
  if (!(pos >= 0 && pos <= doc.content.size)) { throw new RangeError("Position " + pos + " out of range") }
  var path = []
  var start = 0, parentOffset = pos
  for (var node = doc;;) {
    var ref = node.content.findIndex(parentOffset);
      var index = ref.index;
      var offset = ref.offset;
    var rem = parentOffset - offset
    path.push(node, index, start + offset)
    if (!rem) { break }
    node = node.child(index)
    if (node.isText) { break }
    parentOffset = rem - 1
    start += offset + 1
  }
  return new ResolvedPos(pos, path, parentOffset)
};

ResolvedPos.resolveCached = function (doc, pos) {
  for (var i = 0; i < resolveCache.length; i++) {
    var cached = resolveCache[i]
    if (cached.pos == pos && cached.node(0) == doc) { return cached }
  }
  var result = resolveCache[resolveCachePos] = ResolvedPos.resolve(doc, pos)
  resolveCachePos = (resolveCachePos + 1) % resolveCacheSize
  return result
};

Object.defineProperties( ResolvedPos.prototype, prototypeAccessors );
exports.ResolvedPos = ResolvedPos

var resolveCache = [], resolveCachePos = 0, resolveCacheSize = 6

// ::- Represents a flat range of content.
var NodeRange = function($from, $to, depth) {
  // :: ResolvedPos A resolved position along the start of the
  // content. May have a `depth` greater than this object's `depth`
  // property, since these are the positions that were used to
  // compute the range, not re-resolved positions directly at its
  // boundaries.
  this.$from = $from
  // :: ResolvedPos A position along the end of the content. See
  // caveat for [`$from`](#model.NodeRange.$from).
  this.$to = $to
  // :: number The depth of the node that this range points into.
  this.depth = depth
};

var prototypeAccessors$1 = { start: {},end: {},parent: {},startIndex: {},endIndex: {} };

// :: number The position at the start of the range.
prototypeAccessors$1.start.get = function () { return this.$from.before(this.depth + 1) };
// :: number The position at the end of the range.
prototypeAccessors$1.end.get = function () { return this.$to.after(this.depth + 1) };

// :: Node The parent node that the range points into.
prototypeAccessors$1.parent.get = function () { return this.$from.node(this.depth) };
// :: number The start index of the range in the parent node.
prototypeAccessors$1.startIndex.get = function () { return this.$from.index(this.depth) };
// :: number The end index of the range in the parent node.
prototypeAccessors$1.endIndex.get = function () { return this.$to.indexAfter(this.depth) };

Object.defineProperties( NodeRange.prototype, prototypeAccessors$1 );
exports.NodeRange = NodeRange


/***/ }),
/* 18 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Slice = ref.Slice;
var Fragment = ref.Fragment;

var classesById = Object.create(null)

// ::- Superclass for editor selections.
var Selection = function Selection($anchor, $head, ranges) {
  // :: [SelectionRange]
  // The ranges covered by the selection.
  this.ranges = ranges || [new SelectionRange($anchor.min($head), $anchor.max($head))]
  // :: ResolvedPos
  // The resolved anchor of the selection (the side that stays in
  // place when the selection is modified).
  this.$anchor = $anchor
  // :: ResolvedPos
  // The resolved head of the selection (the side that moves when
  // the selection is modified).
  this.$head = $head
};

var prototypeAccessors = { anchor: {},head: {},from: {},to: {},$from: {},$to: {},empty: {} };

// :: number
// The selection's immobile side (does not move when
// shift-selecting).
prototypeAccessors.anchor.get = function () { return this.$anchor.pos };

// :: number
// The selection's mobile side (the side that moves when
// shift-selecting).
prototypeAccessors.head.get = function () { return this.$head.pos };

// :: number
// The lower bound of the selection's first range.
prototypeAccessors.from.get = function () { return this.$from.pos };

// :: number
// The upper bound of the selection's first range.
prototypeAccessors.to.get = function () { return this.$to.pos };

// :: ResolvedPos
// The resolved lowerbound of the selection's main range.
prototypeAccessors.$from.get = function () {
  return this.ranges[0].$from
};

// :: ResolvedPos
// The resolved upper bound of the selection's main range.
prototypeAccessors.$to.get = function () {
  return this.ranges[0].$to
};

// :: bool
// Indicates whether the selection contains any content.
prototypeAccessors.empty.get = function () {
  var ranges = this.ranges
  for (var i = 0; i < ranges.length; i++)
    { if (ranges[i].$from.pos != ranges[i].$to.pos) { return false } }
  return true
};

// eq:: (Selection) → bool
// Test whether the selection is the same as another selection. The
// default implementation tests whether they have the same class,
// head, and anchor.

// map:: (doc: Node, mapping: Mappable) → Selection
// Map this selection through a [mappable](#transform.Mappable) thing. `doc`
// should be the new document, to which we are mapping.

// :: Slice
// Get the content of this selection as a slice.
Selection.prototype.content = function content () {
  return this.$from.node(0).slice(this.from, this.to, true)
};

// :: (Transaction, ?Slice)
// Replace the selection with a slice or, if no slice is given,
// delete the selection. Will append to the given transaction.
Selection.prototype.replace = function replace (tr, content) {
    if ( content === void 0 ) content = Slice.empty;

  // Put the new selection at the position after the inserted
  // content. When that ended in an inline node, search backwards,
  // to get the position after that node. If not, search forward.
  var lastNode = content.content.lastChild, lastParent = null
  for (var i = 0; i < content.openRight; i++) {
    lastParent = lastNode
    lastNode = lastNode.lastChild
  }

  var mapFrom = tr.steps.length, ranges = this.ranges
  for (var i$1 = 0; i$1 < ranges.length; i$1++) {
    var ref = ranges[i$1];
      var $from = ref.$from;
      var $to = ref.$to;
      var mapping = tr.mapping.slice(mapFrom)
    tr.replaceRange(mapping.map($from.pos), mapping.map($to.pos), i$1 ? Slice.empty : content)
    if (i$1 == 0)
      { selectionToInsertionEnd(tr, mapFrom, (lastNode ? lastNode.isInline : lastParent && lastParent.isTextblock) ? -1 : 1) }
  }
};

// :: (Transaction, Node)
// Replace the selection with the given node, appending the changes
// to the given transaction.
Selection.prototype.replaceWith = function replaceWith (tr, node) {
  var mapFrom = tr.steps.length, ranges = this.ranges
  for (var i = 0; i < ranges.length; i++) {
    var ref = ranges[i];
      var $from = ref.$from;
      var $to = ref.$to;
      var mapping = tr.mapping.slice(mapFrom)
    var from = mapping.map($from.pos), to = mapping.map($to.pos)
    if (i) {
      tr.deleteRange(from, to)
    } else {
      tr.replaceRangeWith(from, to, node)
      selectionToInsertionEnd(tr, mapFrom, node.isInline ? -1 : 1)
    }
  }
};

// toJSON:: () → Object
// Convert the selection to a JSON representation. When implementing
// this for a custom selection class, make sure to give the object a
// `type` property whose value matches the ID under which you
// [registered](#state.Selection^jsonID) your class. The default
// implementation adds `type`, `head`, and `anchor` properties.

// :: (ResolvedPos, number, ?bool) → ?Selection
// Find a valid cursor or leaf node selection starting at the given
// position and searching back if `dir` is negative, and forward if
// negative. When `textOnly` is true, only consider cursor
// selections.
Selection.findFrom = function findFrom ($pos, dir, textOnly) {
  var inner = $pos.parent.inlineContent ? new TextSelection($pos)
      : findSelectionIn($pos.node(0), $pos.parent, $pos.pos, $pos.index(), dir, textOnly)
  if (inner) { return inner }

  for (var depth = $pos.depth - 1; depth >= 0; depth--) {
    var found = dir < 0
        ? findSelectionIn($pos.node(0), $pos.node(depth), $pos.before(depth + 1), $pos.index(depth), dir, textOnly)
        : findSelectionIn($pos.node(0), $pos.node(depth), $pos.after(depth + 1), $pos.index(depth) + 1, dir, textOnly)
    if (found) { return found }
  }
};

// :: (ResolvedPos, ?number) → Selection
// Find a valid cursor or leaf node selection near the given
// position. Searches forward first by default, but if `bias` is
// negative, it will search backwards first.
Selection.near = function near ($pos, bias) {
    if ( bias === void 0 ) bias = 1;

  var result = this.findFrom($pos, bias) || this.findFrom($pos, -bias)
  if (!result) { throw new RangeError("Searching for selection in invalid document " + $pos.node(0)) }
  return result
};

// :: (Node, ?bool) → ?Selection
// Find the cursor or leaf node selection closest to the start of
// the given document. When `textOnly` is true, only consider cursor
// selections.
Selection.atStart = function atStart (doc, textOnly) {
  return findSelectionIn(doc, doc, 0, 0, 1, textOnly)
};

// :: (Node, ?bool) → ?Selection
// Find the cursor or leaf node selection closest to the end of
// the given document. When `textOnly` is true, only consider cursor
// selections.
Selection.atEnd = function atEnd (doc, textOnly) {
  return findSelectionIn(doc, doc, doc.content.size, doc.childCount, -1, textOnly)
};

// :: (Node, Object) → Selection
// Deserialize a JSON representation of a selection. Must be
// implemented for custom classes (as a static class method).
Selection.fromJSON = function fromJSON (doc, json) {
  var cls = classesById[json.type]
  if (!cls) { return this.backwardsCompatFromJSON(doc, json) }
  return cls.fromJSON(doc, json)
};

Selection.backwardsCompatFromJSON = function backwardsCompatFromJSON (doc, json) {
  if (json.anchor != null) { return TextSelection.fromJSON(doc, json) }
  if (json.node != null) { return NodeSelection.fromJSON(doc, {anchor: json.node, head: json.after}) }
  throw new RangeError("Unrecognized JSON data " + JSON.stringify(json))
};

// :: (string, constructor<Selection>)
// To be able to deserialize selections from JSON, custom selection
// classes must register themselves with an ID string, so that they
// can be disambiguated. Try to pick something that's unlikely to
// clash with classes from other modules.
Selection.jsonID = function jsonID (id, selectionClass) {
  if (id in classesById) { throw new RangeError("Duplicate use of selection JSON ID " + id) }
  classesById[id] = selectionClass
  selectionClass.prototype.jsonID = id
  return selectionClass
};

// :: () → SelectionBookmark
// Get a [bookmark](#state.SelectionBookmark) for this selection,
// which is a value that can be mapped without having access to a
// current document, and later resolved to a real selection for a
// given document again. (This is used mostly by the history to
// track and restore old selections.) The default implementation of
// this method just converts the selection to a text selection and
// returns the bookmark for that.
Selection.prototype.getBookmark = function getBookmark () {
  return TextSelection.between(this.anchor, this.head).getBookmark()
};

Object.defineProperties( Selection.prototype, prototypeAccessors );
exports.Selection = Selection

// :: bool
// Controls whether, when a selection of this type is active in the
// browser, the selected range should be visible to the user. Defaults
// to `true`.
Selection.prototype.visible = true

// SelectionBookmark:: interface
// A lightweight, document-independent representation of a selection.
// You can define a custom bookmark type for a custom selection class
// to make the history handle it well.
//
//   map:: (mapping: Mapping) → SelectionBookmark
//   Map the bookmark through a set of changes.
//
//   resolve:: (doc: Node) → Selection
//   Resolve the bookmark to a real selection again. This may need to
//   do some error checking and may fall back to a default (usually
//   [`TextSelection.between`](#state.TextSelection.between) if
//   mapping made the bookmark invalid.

// ::- Represents a selected range in a document.
var SelectionRange = function SelectionRange($from, $to) {
  // :: ResolvedPos
  // The lower bound of the range.
  this.$from = $from
  // :: ResolvedPos
  // The upper bound of the range.
  this.$to = $to
};
exports.SelectionRange = SelectionRange

// ::- A text selection represents a classical editor selection, with
// a head (the moving side) and anchor (immobile side), both of which
// point into textblock nodes. It can be empty (a regular cursor
// position).
var TextSelection = (function (Selection) {
  function TextSelection($anchor, $head) {
    if ( $head === void 0 ) $head = $anchor;

    Selection.call(this, $anchor, $head)
  }

  if ( Selection ) TextSelection.__proto__ = Selection;
  TextSelection.prototype = Object.create( Selection && Selection.prototype );
  TextSelection.prototype.constructor = TextSelection;

  var prototypeAccessors$1 = { $cursor: {} };

  // :: ?ResolvedPos
  // Returns a resolved position if this is a cursor selection (an
  // empty text selection), and null otherwise.
  prototypeAccessors$1.$cursor.get = function () { return this.$anchor.pos == this.$head.pos ? this.$head : null };

  TextSelection.prototype.map = function map (doc, mapping) {
    var $head = doc.resolve(mapping.map(this.head))
    if (!$head.parent.inlineContent) { return Selection.near($head) }
    var $anchor = doc.resolve(mapping.map(this.anchor))
    return new TextSelection($anchor.parent.inlineContent ? $anchor : $head, $head)
  };

  TextSelection.prototype.replace = function replace (tr, content) {
    if ( content === void 0 ) content = Slice.empty;

    Selection.prototype.replace.call(this, tr, content)
    if (content == Slice.empty) {
      if (this.$from.parentOffset < this.$from.parent.content.size)
        { tr.ensureMarks(this.$from.marks(true)) }
    }
  };

  TextSelection.prototype.eq = function eq (other) {
    return other instanceof TextSelection && other.anchor == this.anchor && other.head == this.head
  };

  TextSelection.prototype.getBookmark = function getBookmark () {
    return new TextBookmark(this.anchor, this.head)
  };

  TextSelection.prototype.toJSON = function toJSON () {
    return {type: "text", anchor: this.anchor, head: this.head}
  };

  TextSelection.fromJSON = function fromJSON (doc, json) {
    return new TextSelection(doc.resolve(json.anchor), doc.resolve(json.head))
  };

  // :: (Node, number, ?number) → TextSelection
  // Create a text selection from non-resolved positions.
  TextSelection.create = function create (doc, anchor, head) {
    if ( head === void 0 ) head = anchor;

    var $anchor = doc.resolve(anchor)
    return new this($anchor, head == anchor ? $anchor : doc.resolve(head))
  };

  // :: (ResolvedPos, ResolvedPos, ?number) → Selection
  // Return a text selection that spans the given positions or, if
  // they aren't text positions, find a text selection near them.
  // `bias` determines whether the method searches forward (default)
  // or backwards (negative number) first. Will fall back to returning
  // a node selection when the document doesn't contain a valid text
  // position.
  TextSelection.between = function between ($anchor, $head, bias) {
    var dPos = $anchor.pos - $head.pos
    if (!bias || dPos) { bias = dPos >= 0 ? 1 : -1 }
    if (!$head.parent.inlineContent) {
      var found = Selection.findFrom($head, bias, true) || Selection.findFrom($head, -bias, true)
      if (found) { $head = found.$head }
      else { return Selection.near($head, bias) }
    }
    if (!$anchor.parent.inlineContent) {
      if (dPos == 0) {
        $anchor = $head
      } else {
        $anchor = (Selection.findFrom($anchor, -bias, true) || Selection.findFrom($anchor, bias, true)).$anchor
        if (($anchor.pos < $head.pos) != (dPos < 0)) { $anchor = $head }
      }
    }
    return new TextSelection($anchor, $head)
  };

  Object.defineProperties( TextSelection.prototype, prototypeAccessors$1 );

  return TextSelection;
}(Selection));
exports.TextSelection = TextSelection

Selection.jsonID("text", TextSelection)

var TextBookmark = function TextBookmark(anchor, head) {
  this.anchor = anchor
  this.head = head
};
TextBookmark.prototype.map = function map (mapping) {
  return new TextBookmark(mapping.map(this.anchor), mapping.map(this.head))
};
TextBookmark.prototype.resolve = function resolve (doc) {
  return TextSelection.between(doc.resolve(this.anchor), doc.resolve(this.head))
};

// ::- A node selection is a selection that points at a
// single node. All nodes marked [selectable](#model.NodeSpec.selectable)
// can be the target of a node selection. In such an object, `from`
// and `to` point directly before and after the selected node.
var NodeSelection = (function (Selection) {
  function NodeSelection($pos) {
    var node = $pos.nodeAfter
    var $end = $pos.node(0).resolve($pos.pos + node.nodeSize)
    Selection.call(this, $pos, $end)
    // :: Node The selected node.
    this.node = node
  }

  if ( Selection ) NodeSelection.__proto__ = Selection;
  NodeSelection.prototype = Object.create( Selection && Selection.prototype );
  NodeSelection.prototype.constructor = NodeSelection;

  NodeSelection.prototype.map = function map (doc, mapping) {
    var ref = mapping.mapResult(this.anchor);
    var deleted = ref.deleted;
    var pos = ref.pos;
    var $pos = doc.resolve(pos)
    if (deleted) { return Selection.near($pos) }
    return new NodeSelection($pos)
  };

  NodeSelection.prototype.content = function content () {
    return new Slice(Fragment.from(this.node), 0, 0)
  };

  NodeSelection.prototype.eq = function eq (other) {
    return other instanceof NodeSelection && other.anchor == this.anchor
  };

  NodeSelection.prototype.toJSON = function toJSON () {
    return {type: "node", anchor: this.anchor}
  };

  NodeSelection.prototype.getBookmark = function getBookmark () { return new NodeBookmark(this.anchor) };

  NodeSelection.fromJSON = function fromJSON (doc, json) {
    return new NodeSelection(doc.resolve(json.anchor))
  };

  // :: (Node, number, ?number) → TextSelection
  // Create a node selection from non-resolved positions.
  NodeSelection.create = function create (doc, from) {
    return new this(doc.resolve(from))
  };

  // :: (Node) → bool
  // Determines whether the given node may be selected as a node
  // selection.
  NodeSelection.isSelectable = function isSelectable (node) {
    return !node.isText && node.type.spec.selectable !== false
  };

  return NodeSelection;
}(Selection));
exports.NodeSelection = NodeSelection

NodeSelection.prototype.visible = false

Selection.jsonID("node", NodeSelection)

var NodeBookmark = function NodeBookmark(anchor) {
  this.anchor = anchor
};
NodeBookmark.prototype.map = function map (mapping) {
  var ref = mapping.mapResult(this.anchor);
    var deleted = ref.deleted;
    var pos = ref.pos;
  return deleted ? new TextBookmark(pos, pos) : new NodeBookmark(pos)
};
NodeBookmark.prototype.resolve = function resolve (doc) {
  var $pos = doc.resolve(this.anchor), node = $pos.nodeAfter
  if (node && NodeSelection.isSelectable(node)) { return new NodeSelection($pos) }
  return Selection.near($pos)
};

// ::- A selection type that represents selecting the whole document
// (which can not necessarily be expressed with a text selection, when
// there are for example leaf block nodes at the start or end of the
// document).
var AllSelection = (function (Selection) {
  function AllSelection(doc) {
    Selection.call(this, doc.resolve(0), doc.resolve(doc.content.size))
  }

  if ( Selection ) AllSelection.__proto__ = Selection;
  AllSelection.prototype = Object.create( Selection && Selection.prototype );
  AllSelection.prototype.constructor = AllSelection;

  AllSelection.prototype.toJSON = function toJSON () { return {type: "all"} };

  AllSelection.fromJSON = function fromJSON (doc) { return new AllSelection(doc) };

  AllSelection.prototype.map = function map (doc) { return new AllSelection(doc) };

  AllSelection.prototype.eq = function eq (other) { return other instanceof AllSelection };

  AllSelection.prototype.getBookmark = function getBookmark () { return AllBookmark };

  return AllSelection;
}(Selection));
exports.AllSelection = AllSelection

Selection.jsonID("all", AllSelection)

var AllBookmark = {
  map: function map() { return this },
  resolve: function resolve(doc) { return new AllSelection(doc) }
}

// FIXME we'll need some awareness of text direction when scanning for selections

// Try to find a selection inside the given node. `pos` points at the
// position where the search starts. When `text` is true, only return
// text selections.
function findSelectionIn(doc, node, pos, index, dir, text) {
  if (node.inlineContent) { return TextSelection.create(doc, pos) }
  for (var i = index - (dir > 0 ? 0 : 1); dir > 0 ? i < node.childCount : i >= 0; i += dir) {
    var child = node.child(i)
    if (!child.isAtom) {
      var inner = findSelectionIn(doc, child, pos + dir, dir < 0 ? child.childCount : 0, dir, text)
      if (inner) { return inner }
    } else if (!text && NodeSelection.isSelectable(child)) {
      return NodeSelection.create(doc, pos - (dir < 0 ? child.nodeSize : 0))
    }
    pos += child.nodeSize * dir
  }
}

function selectionToInsertionEnd(tr, startLen, bias) {
  if (tr.steps.length == startLen) { return }
  var map = tr.mapping.maps[tr.mapping.maps.length - 1], end
  map.forEach(function (_from, _to, _newFrom, newTo) { return end = newTo; })
  if (end != null) { tr.setSelection(Selection.near(tr.doc.resolve(end), bias)) }
}


/***/ }),
/* 19 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(2);
var Transform = ref.Transform;
var ref$1 = __webpack_require__(0);
var Mark = ref$1.Mark;

var UPDATED_SEL = 1, UPDATED_MARKS = 2, UPDATED_SCROLL = 4

// ::- An editor state transaction, which can be applied to a state to
// create an updated state. Use
// [`EditorState.tr`](#state.EditorState.tr) to create an instance.
//
// Transactions track changes to the document (they are a subclass of
// [`Transform`](#transform.Transform)), but also other state changes,
// like selection updates and adjustments of the set of [stored
// marks](##state.EditorState.storedMarks). In addition, you can store
// metadata properties in a transaction, which are extra pieces of
// information that client code or plugins can use to describe what a
// transacion represents, so that they can update their [own
// state](##state.StateField) accordingly.
//
// The [editor view](##view.EditorView) uses a single metadata
// property: it will attach a property `"pointer"` with the value
// `true` to selection transactions directly caused by mouse or touch
// input.
var Transaction = (function (Transform) {
  function Transaction(state) {
    Transform.call(this, state.doc)
    // :: number
    // The timestamp associated with this transaction.
    this.time = Date.now()
    this.curSelection = state.selection
    // The step count for which the current selection is valid.
    this.curSelectionFor = 0
    // :: ?[Mark]
    // The stored marks in this transaction.
    this.storedMarks = state.storedMarks
    // Bitfield to track which aspects of the state were updated by
    // this transaction.
    this.updated = 0
    // Object used to store metadata properties for the transaction.
    this.meta = Object.create(null)
  }

  if ( Transform ) Transaction.__proto__ = Transform;
  Transaction.prototype = Object.create( Transform && Transform.prototype );
  Transaction.prototype.constructor = Transaction;

  var prototypeAccessors = { selection: {},selectionSet: {},storedMarksSet: {},isGeneric: {},scrolledIntoView: {} };

  // :: Selection
  // The transform's current selection. This defaults to the
  // editor selection [mapped](#state.Selection.map) through the steps in
  // this transform, but can be overwritten with
  // [`setSelection`](#state.Transaction.setSelection).
  prototypeAccessors.selection.get = function () {
    if (this.curSelectionFor < this.steps.length) {
      this.curSelection = this.curSelection.map(this.doc, this.mapping.slice(this.curSelectionFor))
      this.curSelectionFor = this.steps.length
    }
    return this.curSelection
  };

  // :: (Selection) → Transaction
  // Update the transaction's current selection. This will determine
  // the selection that the editor gets when the transaction is
  // applied.
  Transaction.prototype.setSelection = function setSelection (selection) {
    this.curSelection = selection
    this.curSelectionFor = this.steps.length
    this.updated = (this.updated | UPDATED_SEL) & ~UPDATED_MARKS
    this.storedMarks = null
    return this
  };

  // :: bool
  // Whether the selection was explicitly updated by this transaction.
  prototypeAccessors.selectionSet.get = function () {
    return (this.updated & UPDATED_SEL) > 0
  };

  // :: (?[Mark]) → Transaction
  // Set the current stored marks.
  Transaction.prototype.setStoredMarks = function setStoredMarks (marks) {
    this.storedMarks = marks
    this.updated |= UPDATED_MARKS
    return this
  };

  // :: ([Mark]) → Transaction
  // Make sure the current stored marks or, if that is null, the marks
  // at the selection, match the given set of marks. Does nothing if
  // this is already the case.
  Transaction.prototype.ensureMarks = function ensureMarks (marks) {
    if (!Mark.sameSet(this.storedMarks || this.selection.$from.marks(), marks))
      { this.setStoredMarks(marks) }
    return this
  };

  // :: bool
  // Whether the stored marks were explicitly set for this transaction.
  prototypeAccessors.storedMarksSet.get = function () {
    return (this.updated & UPDATED_MARKS) > 0
  };

  Transaction.prototype.addStep = function addStep (step, doc) {
    Transform.prototype.addStep.call(this, step, doc)
    this.updated = this.updated & ~UPDATED_MARKS
    this.storedMarks = null
  };

  // :: (number) → Transaction
  // Update the timestamp for the transaction.
  Transaction.prototype.setTime = function setTime (time) {
    this.time = time
    return this
  };

  // :: (Slice) → Transaction
  Transaction.prototype.replaceSelection = function replaceSelection (slice) {
    this.selection.replace(this, slice)
    return this
  };

  // :: (Node, ?bool) → Transaction
  // Replace the selection with the given node or slice, or delete it
  // if `content` is null. When `inheritMarks` is true and the content
  // is inline, it inherits the marks from the place where it is
  // inserted.
  Transaction.prototype.replaceSelectionWith = function replaceSelectionWith (node, inheritMarks) {
    var selection = this.selection
    if (inheritMarks !== false)
      { node = node.mark(this.storedMarks || selection.$from.marks(selection.to > selection.from)) }
    selection.replaceWith(this, node)
    return this
  };

  // :: () → Transaction
  // Delete the selection.
  Transaction.prototype.deleteSelection = function deleteSelection () {
    this.selection.replace(this)
    return this
  };

  // :: (string, from: ?number, to: ?number) → Transaction
  // Replace the given range, or the selection if no range is given,
  // with a text node containing the given string.
  Transaction.prototype.insertText = function insertText (text, from, to) {
    if ( to === void 0 ) to = from;

    var schema = this.doc.type.schema
    if (from == null) {
      if (!text) { return this.deleteSelection() }
      return this.replaceSelectionWith(schema.text(text), true)
    } else {
      if (!text) { return this.deleteRange(from, to) }
      var node = schema.text(text, this.storedMarks || this.doc.resolve(from).marks(to > from))
      return this.replaceRangeWith(from, to, node)
    }
  };

  // :: (union<string, Plugin, PluginKey>, any) → Transaction
  // Store a metadata property in this transaction, keyed either by
  // name or by plugin.
  Transaction.prototype.setMeta = function setMeta (key, value) {
    this.meta[typeof key == "string" ? key : key.key] = value
    return this
  };

  // :: (union<string, Plugin, PluginKey>) → any
  // Retrieve a metadata property for a given name or plugin.
  Transaction.prototype.getMeta = function getMeta (key) {
    return this.meta[typeof key == "string" ? key : key.key]
  };

  // :: bool
  // Returns true if this transaction doesn't contain any metadata,
  // and can thus be safely extended.
  prototypeAccessors.isGeneric.get = function () {
    var this$1 = this;

    for (var _ in this$1.meta) { return false }
    return true
  };

  // :: () → Transaction
  // Indicate that the editor should scroll the selection into view
  // when updated to the state produced by this transaction.
  Transaction.prototype.scrollIntoView = function scrollIntoView () {
    this.updated |= UPDATED_SCROLL
    return this
  };

  prototypeAccessors.scrolledIntoView.get = function () {
    return (this.updated & UPDATED_SCROLL) > 0
  };

  // :: (Mark) → Transaction
  // Add a mark to the set of stored marks.
  Transaction.prototype.addStoredMark = function addStoredMark (mark) {
    return this.ensureMarks(mark.addToSet(this.storedMarks || this.selection.$head.marks()))
  };

  // :: (union<Mark, MarkType>) → Transaction
  // Remove a mark or mark type from the set of stored marks.
  Transaction.prototype.removeStoredMark = function removeStoredMark (mark) {
    return this.ensureMarks(mark.removeFromSet(this.storedMarks || this.selection.$head.marks()))
  };

  Object.defineProperties( Transaction.prototype, prototypeAccessors );

  return Transaction;
}(Transform));
exports.Transaction = Transaction


/***/ }),
/* 20 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Fragment = ref.Fragment;
var Slice = ref.Slice;
var ref$1 = __webpack_require__(12);
var Step = ref$1.Step;
var StepResult = ref$1.StepResult;

function mapFragment(fragment, f, parent) {
  var mapped = []
  for (var i = 0; i < fragment.childCount; i++) {
    var child = fragment.child(i)
    if (child.content.size) { child = child.copy(mapFragment(child.content, f, child)) }
    if (child.isInline) { child = f(child, parent, i) }
    mapped.push(child)
  }
  return Fragment.fromArray(mapped)
}

// ::- Add a mark to all inline content between two positions.
var AddMarkStep = (function (Step) {
  function AddMarkStep(from, to, mark) {
    Step.call(this)
    this.from = from
    this.to = to
    this.mark = mark
  }

  if ( Step ) AddMarkStep.__proto__ = Step;
  AddMarkStep.prototype = Object.create( Step && Step.prototype );
  AddMarkStep.prototype.constructor = AddMarkStep;

  AddMarkStep.prototype.apply = function apply (doc) {
    var this$1 = this;

    var oldSlice = doc.slice(this.from, this.to), $from = doc.resolve(this.from)
    var parent = $from.node($from.sharedDepth(this.to))
    var slice = new Slice(mapFragment(oldSlice.content, function (node, parent, index) {
      if (!parent.contentMatchAt(index + 1).allowsMark(this$1.mark.type)) { return node }
      return node.mark(this$1.mark.addToSet(node.marks))
    }, parent), oldSlice.openLeft, oldSlice.openRight)
    return StepResult.fromReplace(doc, this.from, this.to, slice)
  };

  AddMarkStep.prototype.invert = function invert () {
    return new RemoveMarkStep(this.from, this.to, this.mark)
  };

  AddMarkStep.prototype.map = function map (mapping) {
    var from = mapping.mapResult(this.from, 1), to = mapping.mapResult(this.to, -1)
    if (from.deleted && to.deleted || from.pos >= to.pos) { return null }
    return new AddMarkStep(from.pos, to.pos, this.mark)
  };

  AddMarkStep.prototype.merge = function merge (other) {
    if (other instanceof AddMarkStep &&
        other.mark.eq(this.mark) &&
        this.from <= other.to && this.to >= other.from)
      { return new AddMarkStep(Math.min(this.from, other.from),
                             Math.max(this.to, other.to), this.mark) }
  };

  AddMarkStep.prototype.offset = function offset (n) {
    return new AddMarkStep(this.from + n, this.to + n, this.mark)
  };

  AddMarkStep.fromJSON = function fromJSON (schema, json) {
    return new AddMarkStep(json.from, json.to, schema.markFromJSON(json.mark))
  };

  return AddMarkStep;
}(Step));
exports.AddMarkStep = AddMarkStep

Step.jsonID("addMark", AddMarkStep)

// ::- Remove a mark from all inline content between two positions.
var RemoveMarkStep = (function (Step) {
  function RemoveMarkStep(from, to, mark) {
    Step.call(this)
    this.from = from
    this.to = to
    this.mark = mark
  }

  if ( Step ) RemoveMarkStep.__proto__ = Step;
  RemoveMarkStep.prototype = Object.create( Step && Step.prototype );
  RemoveMarkStep.prototype.constructor = RemoveMarkStep;

  RemoveMarkStep.prototype.apply = function apply (doc) {
    var this$1 = this;

    var oldSlice = doc.slice(this.from, this.to)
    var slice = new Slice(mapFragment(oldSlice.content, function (node) {
      return node.mark(this$1.mark.removeFromSet(node.marks))
    }), oldSlice.openLeft, oldSlice.openRight)
    return StepResult.fromReplace(doc, this.from, this.to, slice)
  };

  RemoveMarkStep.prototype.invert = function invert () {
    return new AddMarkStep(this.from, this.to, this.mark)
  };

  RemoveMarkStep.prototype.map = function map (mapping) {
    var from = mapping.mapResult(this.from, 1), to = mapping.mapResult(this.to, -1)
    if (from.deleted && to.deleted || from.pos >= to.pos) { return null }
    return new RemoveMarkStep(from.pos, to.pos, this.mark)
  };

  RemoveMarkStep.prototype.merge = function merge (other) {
    if (other instanceof RemoveMarkStep &&
        other.mark.eq(this.mark) &&
        this.from <= other.to && this.to >= other.from)
      { return new RemoveMarkStep(Math.min(this.from, other.from),
                                Math.max(this.to, other.to), this.mark) }
  };

  RemoveMarkStep.prototype.offset = function offset (n) {
    return new RemoveMarkStep(this.from + n, this.to + n, this.mark)
  };

  RemoveMarkStep.fromJSON = function fromJSON (schema, json) {
    return new RemoveMarkStep(json.from, json.to, schema.markFromJSON(json.mark))
  };

  return RemoveMarkStep;
}(Step));
exports.RemoveMarkStep = RemoveMarkStep

Step.jsonID("removeMark", RemoveMarkStep)


/***/ }),
/* 21 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Slice = ref.Slice;
var Fragment = ref.Fragment;

var ref$1 = __webpack_require__(9);
var Transform = ref$1.Transform;
var ref$2 = __webpack_require__(8);
var ReplaceStep = ref$2.ReplaceStep;
var ReplaceAroundStep = ref$2.ReplaceAroundStep;

function canCut(node, start, end) {
  return (start == 0 || node.canReplace(start, node.childCount)) &&
    (end == node.childCount || node.canReplace(0, end))
}

// :: (NodeRange) → ?number
// Try to find a target depth to which the content in the given range
// can be lifted.
function liftTarget(range) {
  var parent = range.parent
  var content = parent.content.cutByIndex(range.startIndex, range.endIndex)
  for (var depth = range.depth;; --depth) {
    var node = range.$from.node(depth), index = range.$from.index(depth), endIndex = range.$to.indexAfter(depth)
    if (depth < range.depth && node.canReplace(index, endIndex, content))
      { return depth }
    if (depth == 0 || !canCut(node, index, endIndex)) { break }
  }
}
exports.liftTarget = liftTarget

// :: (NodeRange, number) → Transform
// Split the content in the given range off from its parent, if there
// is sibling content before or after it, and move it up the tree to
// the depth specified by `target`. You'll probably want to use
// `liftTarget` to compute `target`, in order to be sure the lift is
// valid.
Transform.prototype.lift = function(range, target) {
  var $from = range.$from;
  var $to = range.$to;
  var depth = range.depth;

  var gapStart = $from.before(depth + 1), gapEnd = $to.after(depth + 1)
  var start = gapStart, end = gapEnd

  var before = Fragment.empty, openLeft = 0
  for (var d = depth, splitting = false; d > target; d--)
    { if (splitting || $from.index(d) > 0) {
      splitting = true
      before = Fragment.from($from.node(d).copy(before))
      openLeft++
    } else {
      start--
    } }
  var after = Fragment.empty, openRight = 0
  for (var d$1 = depth, splitting$1 = false; d$1 > target; d$1--)
    { if (splitting$1 || $to.after(d$1 + 1) < $to.end(d$1)) {
      splitting$1 = true
      after = Fragment.from($to.node(d$1).copy(after))
      openRight++
    } else {
      end++
    } }

  return this.step(new ReplaceAroundStep(start, end, gapStart, gapEnd,
                                         new Slice(before.append(after), openLeft, openRight),
                                         before.size - openLeft, true))
}

// :: (NodeRange, NodeType, ?Object) → ?[{type: NodeType, attrs: ?Object}]
// Try to find a valid way to wrap the content in the given range in a
// node of the given type. May introduce extra nodes around and inside
// the wrapper node, if necessary. Returns null if no valid wrapping
// could be found.
function findWrapping(range, nodeType, attrs, innerRange) {
  if ( innerRange === void 0 ) innerRange = range;

  var wrap = {type: nodeType, attrs: attrs}
  var around = findWrappingOutside(range, wrap)
  var inner = around && findWrappingInside(innerRange, wrap)
  if (!inner) { return null }
  return around.concat(wrap).concat(inner)
}
exports.findWrapping = findWrapping

function findWrappingOutside(range, wrap) {
  var parent = range.parent;
  var startIndex = range.startIndex;
  var endIndex = range.endIndex;
  var around = parent.contentMatchAt(startIndex).findWrapping(wrap.type, wrap.attrs)
  if (!around) { return null }
  var outer = around.length ? around[0] : wrap
  if (!parent.canReplaceWith(startIndex, endIndex, outer.type, outer.attrs))
    { return null }
  return around
}

function findWrappingInside(range, wrap) {
  var parent = range.parent;
  var startIndex = range.startIndex;
  var endIndex = range.endIndex;
  var inner = parent.child(startIndex)
  var inside = wrap.type.contentExpr.start(wrap.attrs).findWrappingFor(inner)
  if (!inside) { return null }
  var last = inside.length ? inside[inside.length - 1] : wrap
  var innerMatch = last.type.contentExpr.start(last.attrs)
  for (var i = startIndex; i < endIndex; i++)
    { innerMatch = innerMatch && innerMatch.matchNode(parent.child(i)) }
  if (!innerMatch || !innerMatch.validEnd()) { return null }
  return inside
}

// :: (NodeRange, [{type: NodeType, attrs: ?Object}]) → Transform
// Wrap the given [range](#model.NodeRange) in the given set of wrappers.
// The wrappers are assumed to be valid in this position, and should
// probably be computed with `findWrapping`.
Transform.prototype.wrap = function(range, wrappers) {
  var content = Fragment.empty
  for (var i = wrappers.length - 1; i >= 0; i--)
    { content = Fragment.from(wrappers[i].type.create(wrappers[i].attrs, content)) }

  var start = range.start, end = range.end
  return this.step(new ReplaceAroundStep(start, end, start, end, new Slice(content, 0, 0), wrappers.length, true))
}

// :: (number, ?number, NodeType, ?Object) → Transform
// Set the type of all textblocks (partly) between `from` and `to` to
// the given node type with the given attributes.
Transform.prototype.setBlockType = function(from, to, type, attrs) {
  var this$1 = this;
  if ( to === void 0 ) to = from;

  if (!type.isTextblock) { throw new RangeError("Type given to setBlockType should be a textblock") }
  var mapFrom = this.steps.length
  this.doc.nodesBetween(from, to, function (node, pos) {
    if (node.isTextblock && !node.hasMarkup(type, attrs)) {
      // Ensure all markup that isn't allowed in the new node type is cleared
      this$1.clearNonMatching(this$1.mapping.slice(mapFrom).map(pos, 1), type.contentExpr.start(attrs))
      var mapping = this$1.mapping.slice(mapFrom)
      var startM = mapping.map(pos, 1), endM = mapping.map(pos + node.nodeSize, 1)
      this$1.step(new ReplaceAroundStep(startM, endM, startM + 1, endM - 1,
                                      new Slice(Fragment.from(type.create(attrs)), 0, 0), 1, true))
      return false
    }
  })
  return this
}

// :: (number, ?NodeType, ?Object, ?[Mark]) → Transform
// Change the type and attributes of the node after `pos`.
Transform.prototype.setNodeType = function(pos, type, attrs, marks) {
  var node = this.doc.nodeAt(pos)
  if (!node) { throw new RangeError("No node at given position") }
  if (!type) { type = node.type }
  var newNode = type.create(attrs, null, marks || node.marks)
  if (node.isLeaf)
    { return this.replaceWith(pos, pos + node.nodeSize, newNode) }

  if (!type.validContent(node.content, attrs))
    { throw new RangeError("Invalid content for node type " + type.name) }

  return this.step(new ReplaceAroundStep(pos, pos + node.nodeSize, pos + 1, pos + node.nodeSize - 1,
                                         new Slice(Fragment.from(newNode), 0, 0), 1, true))
}

// :: (Node, number, ?[?{type: NodeType, attrs: ?Object}]) → bool
// Check whether splitting at the given position is allowed.
function canSplit(doc, pos, depth, typesAfter) {
  if ( depth === void 0 ) depth = 1;

  var $pos = doc.resolve(pos), base = $pos.depth - depth
  if (base < 0 ||
      !$pos.parent.canReplace($pos.index(), $pos.parent.childCount) ||
      !$pos.parent.canReplace(0, $pos.indexAfter()))
    { return false }
  for (var d = $pos.depth - 1, i = depth - 1; d > base; d--, i--) {
    var node = $pos.node(d), index$1 = $pos.index(d)
    var typeAfter = typesAfter && typesAfter[i]
    if (!node.canReplace(0, index$1) ||
        !node.canReplaceWith(index$1, node.childCount, typeAfter ? typeAfter.type : $pos.node(d + 1).type,
                             typeAfter ? typeAfter.attrs : $pos.node(d + 1).attrs))
      { return false }
  }
  var index = $pos.indexAfter(base)
  var baseType = typesAfter && typesAfter[0]
  return $pos.node(base).canReplaceWith(index, index, baseType ? baseType.type : $pos.node(base + 1).type,
                                        baseType ? baseType.attrs : $pos.node(base + 1).attrs)
}
exports.canSplit = canSplit

// :: (number, ?number, ?[?{type: NodeType, attrs: ?Object}]) → Transform
// Split the node at the given position, and optionally, if `depth` is
// greater than one, any number of nodes above that. By default, the
// parts split off will inherit the node type of the original node.
// This can be changed by passing an array of types and attributes to
// use after the split.
Transform.prototype.split = function(pos, depth, typesAfter) {
  if ( depth === void 0 ) depth = 1;

  var $pos = this.doc.resolve(pos), before = Fragment.empty, after = Fragment.empty
  for (var d = $pos.depth, e = $pos.depth - depth, i = depth - 1; d > e; d--, i--) {
    before = Fragment.from($pos.node(d).copy(before))
    var typeAfter = typesAfter && typesAfter[i]
    after = Fragment.from(typeAfter ? typeAfter.type.create(typeAfter.attrs, after) : $pos.node(d).copy(after))
  }
  return this.step(new ReplaceStep(pos, pos, new Slice(before.append(after), depth, depth, true)))
}

// :: (Node, number) → bool
// Test whether the blocks before and after a given position can be
// joined.
function canJoin(doc, pos) {
  var $pos = doc.resolve(pos), index = $pos.index()
  return joinable($pos.nodeBefore, $pos.nodeAfter) &&
    $pos.parent.canReplace(index, index + 1)
}
exports.canJoin = canJoin

function joinable(a, b) {
  return a && b && !a.isLeaf && a.canAppend(b)
}

// :: (Node, number, ?number) → ?number
// Find an ancestor of the given position that can be joined to the
// block before (or after if `dir` is positive). Returns the joinable
// point, if any.
function joinPoint(doc, pos, dir) {
  if ( dir === void 0 ) dir = -1;

  var $pos = doc.resolve(pos)
  for (var d = $pos.depth;; d--) {
    var before = (void 0), after = (void 0)
    if (d == $pos.depth) {
      before = $pos.nodeBefore
      after = $pos.nodeAfter
    } else if (dir > 0) {
      before = $pos.node(d + 1)
      after = $pos.node(d).maybeChild($pos.index(d) + 1)
    } else {
      before = $pos.node(d).maybeChild($pos.index(d) - 1)
      after = $pos.node(d + 1)
    }
    if (before && !before.isTextblock && joinable(before, after)) { return pos }
    if (d == 0) { break }
    pos = dir < 0 ? $pos.before(d) : $pos.after(d)
  }
}
exports.joinPoint = joinPoint

// :: (number, ?number, ?bool) → Transform
// Join the blocks around the given position. If depth is 2, their
// last and first siblings are also joined, and so on.
Transform.prototype.join = function(pos, depth) {
  if ( depth === void 0 ) depth = 1;

  var step = new ReplaceStep(pos - depth, pos + depth, Slice.empty, true)
  return this.step(step)
}

// :: (Node, number, NodeType, ?Object) → ?number
// Try to find a point where a node of the given type can be inserted
// near `pos`, by searching up the node hierarchy when `pos` itself
// isn't a valid place but is at the start or end of a node. Return
// null if no position was found.
function insertPoint(doc, pos, nodeType, attrs) {
  var $pos = doc.resolve(pos)
  if ($pos.parent.canReplaceWith($pos.index(), $pos.index(), nodeType, attrs)) { return pos }

  if ($pos.parentOffset == 0)
    { for (var d = $pos.depth - 1; d >= 0; d--) {
      var index = $pos.index(d)
      if ($pos.node(d).canReplaceWith(index, index, nodeType, attrs)) { return $pos.before(d + 1) }
      if (index > 0) { return null }
    } }
  if ($pos.parentOffset == $pos.parent.content.size)
    { for (var d$1 = $pos.depth - 1; d$1 >= 0; d$1--) {
      var index$1 = $pos.indexAfter(d$1)
      if ($pos.node(d$1).canReplaceWith(index$1, index$1, nodeType, attrs)) { return $pos.after(d$1 + 1) }
      if (index$1 < $pos.node(d$1).childCount) { return null }
    } }
}
exports.insertPoint = insertPoint


/***/ }),
/* 22 */
/***/ (function(module, exports) {

function compareObjs(a, b) {
  if (a == b) { return true }
  for (var p in a) { if (a[p] !== b[p]) { return false } }
  for (var p$1 in b) { if (!(p$1 in a)) { return false } }
  return true
}

var WidgetType = function(widget, spec) {
  this.spec = spec || noSpec
  if (!this.spec.raw) {
    if (widget.nodeType != 1) {
      var wrap = document.createElement("span")
      wrap.appendChild(widget)
      widget = wrap
    }
    widget.contentEditable = false
    widget.classList.add("ProseMirror-widget")
  }
  this.widget = widget
};

WidgetType.prototype.map = function (mapping, span, offset, oldOffset) {
  var ref = mapping.mapResult(span.from + oldOffset, this.spec.associative == "left" ? -1 : 1);
    var pos = ref.pos;
    var deleted = ref.deleted;
  return deleted ? null : new Decoration(pos - offset, pos - offset, this)
};

WidgetType.prototype.valid = function () { return true };

WidgetType.prototype.eq = function (other) {
  return this == other ||
    (other instanceof WidgetType && (this.widget == other.widget || this.spec.key) &&
     compareObjs(this.spec, other.spec))
};

var warnedAboutInclusive = false

var InlineType = function(attrs, spec) {
  this.spec = spec || noSpec
  this.attrs = attrs
  if (spec && (spec.inclusiveLeft || spec.inclusiveRight)) {
    if (!spec.inclusiveStart) { spec.inclusiveStart = spec.inclusiveLeft }
    if (!spec.inclusiveEnd) { spec.inclusiveEnd = spec.inclusiveRight }
    if (!warnedAboutInclusive && typeof console != "undefined" && console.warn) {
      warnedAboutInclusive = true
      console.warn("The inclusiveLeft and inclusiveRight options to inline decorations are now called inclusiveStart and inclusiveEnd")
    }
  }
};

InlineType.prototype.map = function (mapping, span, offset, oldOffset) {
  var from = mapping.map(span.from + oldOffset, this.spec.inclusiveStart ? -1 : 1) - offset
  var to = mapping.map(span.to + oldOffset, this.spec.inclusiveEnd ? 1 : -1) - offset
  return from >= to ? null : new Decoration(from, to, this)
};

InlineType.prototype.valid = function (_, span) { return span.from < span.to };

InlineType.prototype.eq = function (other) {
  return this == other ||
    (other instanceof InlineType && compareObjs(this.attrs, other.attrs) &&
     compareObjs(this.spec, other.spec))
};

InlineType.is = function (span) { return span.type instanceof InlineType };

var NodeType = function(attrs, spec) {
  this.spec = spec || noSpec
  this.attrs = attrs
};

NodeType.prototype.map = function (mapping, span, offset, oldOffset) {
  var from = mapping.mapResult(span.from + oldOffset, 1)
  if (from.deleted) { return null }
  var to = mapping.mapResult(span.to + oldOffset, -1)
  if (to.deleted || to.pos <= from.pos) { return null }
  return new Decoration(from.pos - offset, to.pos - offset, this)
};

NodeType.prototype.valid = function (node, span) {
  var ref = node.content.findIndex(span.from);
    var index = ref.index;
    var offset = ref.offset;
  return offset == span.from && offset + node.child(index).nodeSize == span.to
};

NodeType.prototype.eq = function (other) {
  return this == other ||
    (other instanceof NodeType && compareObjs(this.attrs, other.attrs) &&
     compareObjs(this.spec, other.spec))
};

// ::- Decorations can be provided to the view (through the
// [`decorations` prop](#view.EditorProps.decorations)) to adjust the
// way the document is drawn. They come in several variants. See the
// static members of this class for details.
var Decoration = function(from, to, type) {
  this.from = from
  this.to = to
  this.type = type
};

var prototypeAccessors = { spec: {} };

Decoration.prototype.copy = function (from, to) {
  return new Decoration(from, to, this.type)
};

Decoration.prototype.eq = function (other) {
  return this.type.eq(other.type) && this.from == other.from && this.to == other.to
};

Decoration.prototype.map = function (mapping, offset, oldOffset) {
  return this.type.map(mapping, this, offset, oldOffset)
};

// :: (number, dom.Node, ?Object) → Decoration
// Creates a widget decoration, which is a DOM node that's shown in
// the document at the given position.
//
// spec::- These options are supported:
//
//   associative:: ?string
//   By default, widgets are right-associative, meaning they end
//   up to the right of content inserted at their position. You
//   can set this to `"left"` to make it left-associative, so that
//   the inserted content will end up after the widget.
//
//   stopEvent:: ?(event: dom.Event) → bool
//   Can be used to control which DOM events, when they bubble out
//   of this widget, the editor view should ignore.
//
//   key:: ?string
//   When comparing decorations of this type (in order to decide
//   whether it needs to be redrawn), ProseMirror will by default
//   compare the widget DOM node by identity. If you pass a key,
//   that key will be compared instead, which can be useful when
//   you generate decorations on the fly and don't want to store
//   and reuse DOM nodes.
Decoration.widget = function (pos, dom, spec) {
  return new Decoration(pos, pos, new WidgetType(dom, spec))
};

// :: (number, number, DecorationAttrs, ?Object) → Decoration
// Creates an inline decoration, which adds the given attributes to
// each inline node between `from` and `to`.
//
// spec::- These options are recognized:
//
//   inclusiveStart:: ?bool
//   Determines how the left side of the decoration is
//   [mapped](#transform.Position_Mapping) when content is
//   inserted directly at that positon. By default, the decoration
//   won't include the new content, but you can set this to `true`
//   to make it inclusive.
//
//   inclusiveEnd:: ?bool
//   Determines how the right side of the decoration is mapped.
//   See
//   [`inclusiveStart`](#view.Decoration^inline^spec.inclusiveStart).
Decoration.inline = function (from, to, attrs, spec) {
  return new Decoration(from, to, new InlineType(attrs, spec))
};

// :: (number, number, DecorationAttrs, ?Object) → Decoration
// Creates a node decoration. `from` and `to` should point precisely
// before and after a node in the document. That node, and only that
// node, will receive the given attributes.
Decoration.node = function (from, to, attrs, spec) {
  return new Decoration(from, to, new NodeType(attrs, spec))
};

// :: Object
// The spec provided when creating this decoration. Can be useful
// if you've stored extra information in that object.
prototypeAccessors.spec.get = function () { return this.type.spec };

Object.defineProperties( Decoration.prototype, prototypeAccessors );
exports.Decoration = Decoration

// DecorationAttrs:: interface
// A set of attributes to add to a decorated node. Most properties
// simply directly correspond to DOM attributes of the same name,
// which will be set to the property's value. These are exceptions:
//
//   class:: ?string
//   A CSS class name or a space-separated set of class names to be
//   _added_ to the classes that the node already had.
//
//   style:: ?string
//   A string of CSS to be _added_ to the node's existing `style` property.
//
//   nodeName:: ?string
//   When non-null, the target node is wrapped in a DOM element of
//   this type (and the other attributes are applied to this element).

var none = [], noSpec = {}

// ::- A collection of [decorations](#view.Decoration), organized in
// such a way that the drawing algorithm can efficiently use and
// compare them. This is a persistent data structure—it is not
// modified, updates create a new value.
var DecorationSet = function(local, children) {
  this.local = local && local.length ? local : none
  this.children = children && children.length ? children : none
};

// :: (Node, [Decoration]) → DecorationSet
// Create a set of decorations, using the structure of the given
// document.
DecorationSet.create = function (doc, decorations) {
  return decorations.length ? buildTree(decorations, doc, 0, noSpec) : empty
};

// :: (?number, ?number) → [Decoration]
// Find all decorations in this set which touch the given range
// (including decorations that start or end directly at the
// boundaries). When the arguments are omitted, all decorations in
// the set are collected.
DecorationSet.prototype.find = function (start, end) {
  var result = []
  this.findInner(start == null ? 0 : start, end == null ? 1e9 : end, result, 0)
  return result
};

DecorationSet.prototype.findInner = function (start, end, result, offset) {
    var this$1 = this;

  for (var i = 0; i < this.local.length; i++) {
    var span = this$1.local[i]
    if (span.from <= end && span.to >= start)
      { result.push(span.copy(span.from + offset, span.to + offset)) }
  }
  for (var i$1 = 0; i$1 < this.children.length; i$1 += 3) {
    if (this$1.children[i$1] < end && this$1.children[i$1 + 1] > start) {
      var childOff = this$1.children[i$1] + 1
      this$1.children[i$1 + 2].findInner(start - childOff, end - childOff, result, offset + childOff)
    }
  }
};

// :: (Mapping, Node, ?Object) → DecorationSet
// Map the set of decorations in response to a change in the
// document.
//
// options::- An optional set of options.
//
//   onRemove:: ?(decorationSpec: Object)
//   When given, this function will be called for each decoration
//   that gets dropped as a result of the mapping, passing the
//   spec of that decoration.
DecorationSet.prototype.map = function (mapping, doc, options) {
  if (this == empty || mapping.maps.length == 0) { return this }
  return this.mapInner(mapping, doc, 0, 0, options || noSpec)
};

DecorationSet.prototype.mapInner = function (mapping, node, offset, oldOffset, options) {
    var this$1 = this;

  var newLocal
  for (var i = 0; i < this.local.length; i++) {
    var mapped = this$1.local[i].map(mapping, offset, oldOffset)
    if (mapped && mapped.type.valid(node, mapped)) { (newLocal || (newLocal = [])).push(mapped) }
    else if (options.onRemove) { options.onRemove(this$1.local[i].spec) }
  }

  if (this.children.length)
    { return mapChildren(this.children, newLocal, mapping, node, offset, oldOffset, options) }
  else
    { return newLocal ? new DecorationSet(newLocal.sort(byPos)) : empty }
};

// :: (Node, [Decoration]) → DecorationSet
// Add the given array of decorations to the ones in the set,
// producing a new set. Needs access to the current document to
// create the appropriate tree structure.
DecorationSet.prototype.add = function (doc, decorations) {
  if (!decorations.length) { return this }
  if (this == empty) { return DecorationSet.create(doc, decorations) }
  return this.addInner(doc, decorations, 0)
};

DecorationSet.prototype.addInner = function (doc, decorations, offset) {
    var this$1 = this;

  var children, childIndex = 0
  doc.forEach(function (childNode, childOffset) {
    var baseOffset = childOffset + offset, found
    if (!(found = takeSpansForNode(decorations, childNode, baseOffset))) { return }

    if (!children) { children = this$1.children.slice() }
    while (childIndex < children.length && children[childIndex] < childOffset) { childIndex += 3 }
    if (children[childIndex] == childOffset)
      { children[childIndex + 2] = children[childIndex + 2].addInner(childNode, found, baseOffset + 1) }
    else
      { children.splice(childIndex, 0, childOffset, childOffset + childNode.nodeSize, buildTree(found, childNode, baseOffset + 1, noSpec)) }
    childIndex += 3
  })

  var local = moveSpans(childIndex ? withoutNulls(decorations) : decorations, -offset)
  return new DecorationSet(local.length ? this.local.concat(local).sort(byPos) : this.local,
                           children || this.children)
};

// :: ([Decoration]) → DecorationSet
// Create a new set that contains the decorations in this set, minus
// the ones in the given array.
DecorationSet.prototype.remove = function (decorations) {
  if (decorations.length == 0 || this == empty) { return this }
  return this.removeInner(decorations, 0)
};

DecorationSet.prototype.removeInner = function (decorations, offset) {
    var this$1 = this;

  var children = this.children, local = this.local
  for (var i = 0; i < children.length; i += 3) {
    var found = (void 0), from = children[i] + offset, to = children[i + 1] + offset
    for (var j = 0, span = (void 0); j < decorations.length; j++) { if (span = decorations[j]) {
      if (span.from > from && span.to < to) {
        decorations[j] = null
        ;(found || (found = [])).push(span)
      }
    } }
    if (!found) { continue }
    if (children == this$1.children) { children = this$1.children.slice() }
    var removed = children[i + 2].removeInner(found, from + 1)
    if (removed != empty) {
      children[i + 2] = removed
    } else {
      children.splice(i, 3)
      i -= 3
    }
  }
  if (local.length) { for (var i$1 = 0, span$1 = (void 0); i$1 < decorations.length; i$1++) { if (span$1 = decorations[i$1]) {
    for (var j$1 = 0; j$1 < local.length; j$1++) { if (local[j$1].type == span$1.type) {
      if (local == this$1.local) { local = this$1.local.slice() }
      local.splice(j$1--, 1)
    } }
  } } }
  if (children == this.children && local == this.local) { return this }
  return local.length || children.length ? new DecorationSet(local, children) : empty
};

DecorationSet.prototype.forChild = function (offset, node) {
    var this$1 = this;

  if (this == empty) { return this }
  if (node.isLeaf) { return DecorationSet.empty }

  var child, local
  for (var i = 0; i < this.children.length; i += 3) { if (this$1.children[i] >= offset) {
    if (this$1.children[i] == offset) { child = this$1.children[i + 2] }
    break
  } }
  var start = offset + 1, end = start + node.content.size
  for (var i$1 = 0; i$1 < this.local.length; i$1++) {
    var dec = this$1.local[i$1]
    if (dec.from < end && dec.to > start && (dec.type instanceof InlineType)) {
      var from = Math.max(start, dec.from) - start, to = Math.min(end, dec.to) - start
      if (from < to) { (local || (local = [])).push(dec.copy(from, to)) }
    }
  }
  if (local) {
    var localSet = new DecorationSet(local)
    return child ? new DecorationGroup([localSet, child]) : localSet
  }
  return child || empty
};

DecorationSet.prototype.eq = function (other) {
    var this$1 = this;

  if (this == other) { return true }
  if (!(other instanceof DecorationSet) ||
      this.local.length != other.local.length ||
      this.children.length != other.children.length) { return false }
  for (var i = 0; i < this.local.length; i++)
    { if (!this$1.local[i].eq(other.local[i])) { return false } }
  for (var i$1 = 0; i$1 < this.children.length; i$1 += 3)
    { if (this$1.children[i$1] != other.children[i$1] ||
        this$1.children[i$1 + 1] != other.children[i$1 + 1] ||
        !this$1.children[i$1 + 2].eq(other.children[i$1 + 2])) { return false } }
  return false
};

DecorationSet.prototype.locals = function (node) {
  return removeOverlap(this.localsInner(node))
};

DecorationSet.prototype.localsInner = function (node) {
    var this$1 = this;

  if (this == empty) { return none }
  if (node.inlineContent || !this.local.some(InlineType.is)) { return this.local }
  var result = []
  for (var i = 0; i < this.local.length; i++) {
    if (!(this$1.local[i].type instanceof InlineType))
      { result.push(this$1.local[i]) }
  }
  return result
};
exports.DecorationSet = DecorationSet

var empty = new DecorationSet()

// :: DecorationSet
// The empty set of decorations.
DecorationSet.empty = empty

// :- An abstraction that allows the code dealing with decorations to
// treat multiple DecorationSet objects as if it were a single object
// with (a subset of) the same interface.
var DecorationGroup = function(members) {
  this.members = members
};

DecorationGroup.prototype.forChild = function (offset, child) {
    var this$1 = this;

  if (child.isLeaf) { return DecorationSet.empty }
  var found = []
  for (var i = 0; i < this.members.length; i++) {
    var result = this$1.members[i].forChild(offset, child)
    if (result == empty) { continue }
    if (result instanceof DecorationGroup) { found = found.concat(result.members) }
    else { found.push(result) }
  }
  return DecorationGroup.from(found)
};

DecorationGroup.prototype.eq = function (other) {
    var this$1 = this;

  if (!(other instanceof DecorationGroup) ||
      other.members.length != this.members.length) { return false }
  for (var i = 0; i < this.members.length; i++)
    { if (!this$1.members[i].eq(other.members[i])) { return false } }
  return true
};

DecorationGroup.prototype.locals = function (node) {
    var this$1 = this;

  var result, sorted = true
  for (var i = 0; i < this.members.length; i++) {
    var locals = this$1.members[i].localsInner(node)
    if (!locals.length) { continue }
    if (!result) {
      result = locals
    } else {
      if (sorted) {
        result = result.slice()
        sorted = false
      }
      for (var j = 0; j < locals.length; j++) { result.push(locals[j]) }
    }
  }
  return result ? removeOverlap(sorted ? result : result.sort(byPos)) : none
};

// : ([DecorationSet]) → union<DecorationSet, DecorationGroup>
// Create a group for the given array of decoration sets, or return
// a single set when possible.
DecorationGroup.from = function (members) {
  switch (members.length) {
    case 0: return empty
    case 1: return members[0]
    default: return new DecorationGroup(members)
  }
};

function mapChildren(oldChildren, newLocal, mapping, node, offset, oldOffset, options) {
  var children = oldChildren.slice()

  // Mark the children that are directly touched by changes, and
  // move those that are after the changes.
  var shift = function (oldStart, oldEnd, newStart, newEnd) {
    for (var i = 0; i < children.length; i += 3) {
      var end = children[i + 1], dSize = (void 0)
      if (end == -1 || oldStart > end + oldOffset) { continue }
      if (oldEnd >= children[i] + oldOffset) {
        children[i + 1] = -1
      } else if (dSize = (newEnd - newStart) - (oldEnd - oldStart)) {
        children[i] += dSize
        children[i + 1] += dSize
      }
    }
  }
  for (var i = 0; i < mapping.maps.length; i++) { mapping.maps[i].forEach(shift) }

  // Find the child nodes that still correspond to a single node,
  // recursively call mapInner on them and update their positions.
  var mustRebuild = false
  for (var i$1 = 0; i$1 < children.length; i$1 += 3) { if (children[i$1 + 1] == -1) { // Touched nodes
    var from = mapping.map(children[i$1] + oldOffset), fromLocal = from - offset
    if (fromLocal < 0 || fromLocal >= node.content.size) {
      mustRebuild = true
      continue
    }
    // Must read oldChildren because children was tagged with -1
    var to = mapping.map(oldChildren[i$1 + 1] + oldOffset, -1), toLocal = to - offset
    var ref = node.content.findIndex(fromLocal);
    var index = ref.index;
    var childOffset = ref.offset;
    var childNode = node.maybeChild(index)
    if (childNode && childOffset == fromLocal && childOffset + childNode.nodeSize == toLocal) {
      var mapped = children[i$1 + 2].mapInner(mapping, childNode, from + 1, children[i$1] + oldOffset + 1, options)
      if (mapped != empty) {
        children[i$1] = fromLocal
        children[i$1 + 1] = toLocal
        children[i$1 + 2] = mapped
      } else {
        children.splice(i$1, 3)
        i$1 -= 3
      }
    } else {
      mustRebuild = true
    }
  } }

  // Remaining children must be collected and rebuilt into the appropriate structure
  if (mustRebuild) {
    var decorations = mapAndGatherRemainingDecorations(children, newLocal ? moveSpans(newLocal, offset) : [], mapping, oldOffset, options)
    var built = buildTree(decorations, node, 0, options)
    newLocal = built.local
    for (var i$2 = 0; i$2 < children.length; i$2 += 3) { if (children[i$2 + 1] == -1) {
      children.splice(i$2, 3)
      i$2 -= 3
    } }
    for (var i$3 = 0, j = 0; i$3 < built.children.length; i$3 += 3) {
      var from$1 = built.children[i$3]
      while (j < children.length && children[j] < from$1) { j += 3 }
      children.splice(j, 0, built.children[i$3], built.children[i$3 + 1], built.children[i$3 + 2])
    }
  }

  return new DecorationSet(newLocal && newLocal.sort(byPos), children)
}

function moveSpans(spans, offset) {
  if (!offset || !spans.length) { return spans }
  var result = []
  for (var i = 0; i < spans.length; i++) {
    var span = spans[i]
    result.push(new Decoration(span.from + offset, span.to + offset, span.type))
  }
  return result
}

function mapAndGatherRemainingDecorations(children, decorations, mapping, oldOffset, options) {
  // Gather all decorations from the remaining marked children
  function gather(set, oldOffset) {
    for (var i = 0; i < set.local.length; i++) {
      var mapped = set.local[i].map(mapping, 0, oldOffset)
      if (mapped) { decorations.push(mapped) }
      else if (options.onRemove) { options.onRemove(set.local[i].spec) }
    }
    for (var i$1 = 0; i$1 < set.children.length; i$1 += 3)
      { gather(set.children[i$1 + 2], set.children[i$1] + oldOffset + 1) }
  }
  for (var i = 0; i < children.length; i += 3) { if (children[i + 1] == -1)
    { gather(children[i + 2], children[i] + oldOffset + 1) } }

  return decorations
}

function takeSpansForNode(spans, node, offset) {
  if (node.isLeaf) { return null }
  var end = offset + node.nodeSize, found = null
  for (var i = 0, span = (void 0); i < spans.length; i++) {
    if ((span = spans[i]) && span.from > offset && span.to < end) {
      ;(found || (found = [])).push(span)
      spans[i] = null
    }
  }
  return found
}

function withoutNulls(array) {
  var result = []
  for (var i = 0; i < array.length; i++)
    { if (array[i] != null) { result.push(array[i]) } }
  return result
}

// : ([Decoration], Node, number) → DecorationSet
// Build up a tree that corresponds to a set of decorations. `offset`
// is a base offset that should be subtractet from the `from` and `to`
// positions in the spans (so that we don't have to allocate new spans
// for recursive calls).
function buildTree(spans, node, offset, options) {
  var children = [], hasNulls = false
  node.forEach(function (childNode, localStart) {
    var found = takeSpansForNode(spans, childNode, localStart + offset)
    if (found) {
      hasNulls = true
      var subtree = buildTree(found, childNode, offset + localStart + 1, options)
      if (subtree != empty)
        { children.push(localStart, localStart + childNode.nodeSize, subtree) }
    }
  })
  var locals = moveSpans(hasNulls ? withoutNulls(spans) : spans, -offset).sort(byPos)
  for (var i = 0; i < locals.length; i++) { if (!locals[i].type.valid(node, locals[i])) {
    if (options.onRemove) { options.onRemove(locals[i].spec) }
    locals.splice(i--, 1)
  } }
  return locals.length || children.length ? new DecorationSet(locals, children) : empty
}

// : (Decoration, Decoration) → number
// Used to sort decorations so that ones with a low start position
// come first, and within a set with the same start position, those
// with an smaller end position come first.
function byPos(a, b) {
  return a.from - b.from || a.to - b.to
}

// : ([Decoration]) → [Decoration]
// Scan a sorted array of decorations for partially overlapping spans,
// and split those so that only fully overlapping spans are left (to
// make subsequent rendering easier). Will return the input array if
// no partially overlapping spans are found (the common case).
function removeOverlap(spans) {
  var working = spans
  for (var i = 0; i < working.length - 1; i++) {
    var span = working[i]
    if (span.from != span.to) { for (var j = i + 1; j < working.length; j++) {
      var next = working[j]
      if (next.from == span.from) {
        if (next.to != span.to) {
          if (working == spans) { working = spans.slice() }
          // Followed by a partially overlapping larger span. Split that
          // span.
          working[j] = next.copy(next.from, span.to)
          insertAhead(working, j + 1, next.copy(span.to, next.to))
        }
        continue
      } else {
        if (next.from < span.to) {
          if (working == spans) { working = spans.slice() }
          // The end of this one overlaps with a subsequent span. Split
          // this one.
          working[i] = span.copy(span.from, next.from)
          insertAhead(working, j, span.copy(next.from, span.to))
        }
        break
      }
    } }
  }
  return working
}
exports.removeOverlap = removeOverlap

function insertAhead(array, i, deco) {
  while (i < array.length && byPos(deco, array[i]) > 0) { i++ }
  array.splice(i, 0, deco)
}

// : (EditorView) → union<DecorationSet, DecorationGroup>
// Get the decorations associated with the current props of a view.
function viewDecorations(view) {
  var found = []
  view.someProp("decorations", function (f) {
    var result = f(view.state)
    if (result && result != empty) { found.push(result) }
  })
  if (view.cursorWrapper)
    { found.push(DecorationSet.create(view.state.doc, [view.cursorWrapper])) }
  return DecorationGroup.from(found)
}
exports.viewDecorations = viewDecorations


/***/ }),
/* 23 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Fragment = ref.Fragment;
var DOMParser = ref.DOMParser;
var ref$1 = __webpack_require__(1);
var Selection = ref$1.Selection;
var ref$2 = __webpack_require__(2);
var Mapping = ref$2.Mapping;

var ref$3 = __webpack_require__(46);
var TrackMappings = ref$3.TrackMappings;
var ref$4 = __webpack_require__(13);
var selectionBetween = ref$4.selectionBetween;
var ref$5 = __webpack_require__(6);
var selectionCollapsed = ref$5.selectionCollapsed;

var DOMChange = function(view, composing) {
  var this$1 = this;

  this.view = view
  this.state = view.state
  this.composing = composing
  this.from = this.to = null
  this.timeout = composing ? null : setTimeout(function () { return this$1.finish(); }, 20)
  this.trackMappings = new TrackMappings(view.state)

  // If there have been changes since this DOM update started, we must
  // map our start and end positions, as well as the new selection
  // positions, through them. This tracks that mapping.
  this.mapping = new Mapping
  this.mappingTo = view.state
};

DOMChange.prototype.addRange = function (from, to) {
  if (this.from == null) {
    this.from = from
    this.to = to
  } else {
    this.from = Math.min(from, this.from)
    this.to = Math.max(to, this.to)
  }
};

DOMChange.prototype.changedRange = function () {
  if (this.from == null) { return rangeAroundSelection(this.state.selection) }
  var $from = this.state.doc.resolve(Math.min(this.from, this.state.selection.from)), $to = this.state.doc.resolve(this.to)
  var shared = $from.sharedDepth(this.to)
  return {from: $from.before(shared + 1), to: $to.after(shared + 1)}
};

DOMChange.prototype.markDirty = function (range) {
  if (this.from == null) { this.view.docView.markDirty((range = range || this.changedRange()).from, range.to) }
  else { this.view.docView.markDirty(this.from, this.to) }
};

DOMChange.prototype.stateUpdated = function (state) {
  if (this.trackMappings.getMapping(state, this.mapping)) {
    this.trackMappings.destroy()
    this.trackMappings = new TrackMappings(state)
    this.mappingTo = state
    return true
  } else {
    this.markDirty()
    this.destroy()
    return false
  }
};

DOMChange.prototype.finish = function (force) {
  clearTimeout(this.timeout)
  if (this.composing && !force) { return }
  this.view.domObserver.flush()
  var range = this.changedRange()
  this.markDirty(range)

  this.destroy()
  readDOMChange(this.view, this.mapping, this.state, range)

  // If the reading didn't result in a view update, force one by
  // resetting the view to its current state.
  if (this.view.docView.dirty) { this.view.updateState(this.view.state) }
};

DOMChange.prototype.destroy = function () {
  clearTimeout(this.timeout)
  this.trackMappings.destroy()
  this.view.inDOMChange = null
};

DOMChange.prototype.compositionEnd = function () {
    var this$1 = this;

  if (this.composing) {
    this.composing = false
    this.timeout = setTimeout(function () { return this$1.finish(); }, 50)
  }
};

DOMChange.start = function (view, composing) {
  if (view.inDOMChange) {
    if (composing) {
      clearTimeout(view.inDOMChange.timeout)
      view.inDOMChange.composing = true
    }
  } else {
    view.inDOMChange = new DOMChange(view, composing)
  }
  return view.inDOMChange
};
exports.DOMChange = DOMChange

// Note that all referencing and parsing is done with the
// start-of-operation selection and document, since that's the one
// that the DOM represents. If any changes came in in the meantime,
// the modification is mapped over those before it is applied, in
// readDOMChange.

function parseBetween(view, oldState, range) {
  var ref = view.docView.parseRange(range.from, range.to);
  var parent = ref.node;
  var fromOffset = ref.fromOffset;
  var toOffset = ref.toOffset;
  var from = ref.from;
  var to = ref.to;

  var domSel = view.root.getSelection(), find = null, anchor = domSel.anchorNode
  if (anchor && view.dom.contains(anchor.nodeType == 1 ? anchor : anchor.parentNode)) {
    find = [{node: anchor, offset: domSel.anchorOffset}]
    if (!selectionCollapsed(domSel))
      { find.push({node: domSel.focusNode, offset: domSel.focusOffset}) }
  }
  var startDoc = oldState.doc
  var parser = view.someProp("domParser") || DOMParser.fromSchema(view.state.schema)
  var $from = startDoc.resolve(from)
  var sel = null, doc = parser.parse(parent, {
    topNode: $from.parent.copy(),
    topStart: $from.index(),
    topOpen: true,
    from: fromOffset,
    to: toOffset,
    preserveWhitespace: $from.parent.type.spec.code ? "full" : true,
    editableContent: true,
    findPositions: find,
    ruleFromNode: ruleFromNode,
    context: $from
  })
  if (find && find[0].pos != null) {
    var anchor$1 = find[0].pos, head = find[1] && find[1].pos
    if (head == null) { head = anchor$1 }
    sel = {anchor: anchor$1 + from, head: head + from}
  }
  return {doc: doc, sel: sel, from: from, to: to}
}

function ruleFromNode(dom) {
  var desc = dom.pmViewDesc
  if (desc) { return desc.parseRule() }
  else if (dom.nodeName == "BR" && dom.parentNode && dom.parentNode.lastChild == dom) { return {ignore: true} }
}

function isAtEnd($pos, depth) {
  for (var i = depth || 0; i < $pos.depth; i++)
    { if ($pos.index(i) + 1 < $pos.node(i).childCount) { return false } }
  return $pos.parentOffset == $pos.parent.content.size
}
function isAtStart($pos, depth) {
  for (var i = depth || 0; i < $pos.depth; i++)
    { if ($pos.index(0) > 0) { return false } }
  return $pos.parentOffset == 0
}

function rangeAroundSelection(selection) {
  // Intentionally uses $head/$anchor because those will correspond to the DOM selection
  var $from = selection.$anchor.min(selection.$head), $to = selection.$anchor.max(selection.$head)

  if ($from.sameParent($to) && $from.parent.inlineContent && $from.parentOffset && $to.parentOffset < $to.parent.content.size) {
    var startOff = Math.max(0, $from.parentOffset)
    var size = $from.parent.content.size
    var endOff = Math.min(size, $to.parentOffset)

    if (startOff > 0)
      { startOff = $from.parent.childBefore(startOff).offset }
    if (endOff < size) {
      var after = $from.parent.childAfter(endOff)
      endOff = after.offset + after.node.nodeSize
    }
    var nodeStart = $from.start()
    return {from: nodeStart + startOff, to: nodeStart + endOff}
  } else {
    for (var depth = 0;; depth++) {
      var fromStart = isAtStart($from, depth + 1), toEnd = isAtEnd($to, depth + 1)
      if (fromStart || toEnd || $from.index(depth) != $to.index(depth) || $to.node(depth).isTextblock) {
        var from = $from.before(depth + 1), to = $to.after(depth + 1)
        if (fromStart && $from.index(depth) > 0)
          { from -= $from.node(depth).child($from.index(depth) - 1).nodeSize }
        if (toEnd && $to.index(depth) + 1 < $to.node(depth).childCount)
          { to += $to.node(depth).child($to.index(depth) + 1).nodeSize }
        return {from: from, to: to}
      }
    }
  }
}

function keyEvent(keyCode, key) {
  var event = document.createEvent("Event")
  event.initEvent("keydown", true, true)
  event.keyCode = keyCode
  event.key = event.code = key
  return event
}

function readDOMChange(view, mapping, oldState, range) {
  var parse = parseBetween(view, oldState, range)

  var doc = oldState.doc, compare = doc.slice(parse.from, parse.to)
  var change = findDiff(compare.content, parse.doc.content, parse.from, oldState.selection.from)

  if (!change) {
    if (parse.sel) {
      var sel = resolveSelection(view, view.state.doc, mapping, parse.sel)
      if (sel && !sel.eq(view.state.selection)) { view.dispatch(view.state.tr.setSelection(sel)) }
    }
    return
  }

  var $from = parse.doc.resolveNoCache(change.start - parse.from)
  var $to = parse.doc.resolveNoCache(change.endB - parse.from)
  var nextSel
  // If this looks like the effect of pressing Enter, just dispatch an
  // Enter key instead.
  if (!$from.sameParent($to) && $from.pos < parse.doc.content.size &&
      (nextSel = Selection.findFrom(parse.doc.resolve($from.pos + 1), 1, true)) &&
      nextSel.head == $to.pos &&
      view.someProp("handleKeyDown", function (f) { return f(view, keyEvent(13, "Enter")); }))
    { return }
  // Same for backspace
  if (oldState.selection.anchor > change.start &&
      looksLikeJoin(doc, change.start, change.endA, $from, $to) &&
      view.someProp("handleKeyDown", function (f) { return f(view, keyEvent(8, "Backspace")); }))
    { return }

  var from = mapping.map(change.start), to = mapping.map(change.endA, -1)

  var tr, storedMarks, markChange, $from1
  if ($from.sameParent($to) && $from.parent.inlineContent) {
    if ($from.pos == $to.pos) { // Deletion
      tr = view.state.tr.delete(from, to)
      var $start = doc.resolve(change.start)
      if ($start.parentOffset < $start.parent.content.size) { storedMarks = $start.marks(true) }
    } else if ( // Adding or removing a mark
      change.endA == change.endB && ($from1 = doc.resolve(change.start)) &&
      (markChange = isMarkChange($from.parent.content.cut($from.parentOffset, $to.parentOffset),
                                 $from1.parent.content.cut($from1.parentOffset, change.endA - $from1.start())))
    ) {
      tr = view.state.tr
      if (markChange.type == "add") { tr.addMark(from, to, markChange.mark) }
      else { tr.removeMark(from, to, markChange.mark) }
    } else if ($from.parent.child($from.index()).isText && $from.index() == $to.index() - ($to.textOffset ? 0 : 1)) {
      // Both positions in the same text node -- simply insert text
      var text = $from.parent.textBetween($from.parentOffset, $to.parentOffset)
      if (view.someProp("handleTextInput", function (f) { return f(view, from, to, text); })) { return }
      tr = view.state.tr.insertText(text, from, to)
    }
  }

  if (!tr)
    { tr = view.state.tr.replace(from, to, parse.doc.slice(change.start - parse.from, change.endB - parse.from)) }
  if (parse.sel) {
    var sel$1 = resolveSelection(view, tr.doc, mapping, parse.sel)
    if (sel$1) { tr.setSelection(sel$1) }
  }
  if (storedMarks) { tr.ensureMarks(storedMarks) }
  view.dispatch(tr.scrollIntoView())
}

function resolveSelection(view, doc, mapping, parsedSel) {
  if (Math.max(parsedSel.anchor, parsedSel.head) > doc.content.size) { return null }
  return selectionBetween(view, doc.resolve(mapping.map(parsedSel.anchor)),
                          doc.resolve(mapping.map(parsedSel.head)))
}

// : (Fragment, Fragment) → ?{mark: Mark, type: string}
// Given two same-length, non-empty fragments of inline content,
// determine whether the first could be created from the second by
// removing or adding a single mark type.
function isMarkChange(cur, prev) {
  var curMarks = cur.firstChild.marks, prevMarks = prev.firstChild.marks
  var added = curMarks, removed = prevMarks, type, mark, update
  for (var i = 0; i < prevMarks.length; i++) { added = prevMarks[i].removeFromSet(added) }
  for (var i$1 = 0; i$1 < curMarks.length; i$1++) { removed = curMarks[i$1].removeFromSet(removed) }
  if (added.length == 1 && removed.length == 0) {
    mark = added[0]
    type = "add"
    update = function (node) { return node.mark(mark.addToSet(node.marks)); }
  } else if (added.length == 0 && removed.length == 1) {
    mark = removed[0]
    type = "remove"
    update = function (node) { return node.mark(mark.removeFromSet(node.marks)); }
  } else {
    return null
  }
  var updated = []
  for (var i$2 = 0; i$2 < prev.childCount; i$2++) { updated.push(update(prev.child(i$2))) }
  if (Fragment.from(updated).eq(cur)) { return {mark: mark, type: type} }
}

function looksLikeJoin(old, start, end, $newStart, $newEnd) {
  if (!$newStart.parent.isTextblock ||
      // The content must have shrunk
      end - start <= $newEnd.pos - $newStart.pos ||
      // newEnd must point directly at or after the end of the block that newStart points into
      skipClosingAndOpening($newStart, true, false) < $newEnd.pos)
    { return false }

  var $start = old.resolve(start)
  // Start must be at the end of a block
  if ($start.parentOffset < $start.parent.content.size || !$start.parent.isTextblock)
    { return false }
  var $next = old.resolve(skipClosingAndOpening($start, true, true))
  // The next textblock must start before end and end near it
  if (!$next.parent.isTextblock || $next.pos > end ||
      skipClosingAndOpening($next, true, false) < end)
    { return false }

  // The fragments after the join point must match
  return $newStart.parent.content.cut($newStart.parentOffset).eq($next.parent.content)
}

function skipClosingAndOpening($pos, fromEnd, mayOpen) {
  var depth = $pos.depth, end = fromEnd ? $pos.end() : $pos.pos
  while (depth > 0 && (fromEnd || $pos.indexAfter(depth) == $pos.node(depth).childCount)) {
    depth--
    end++
    fromEnd = false
  }
  if (mayOpen) {
    var next = $pos.node(depth).maybeChild($pos.indexAfter(depth))
    while (next && !next.isLeaf) {
      next = next.firstChild
      end++
    }
  }
  return end
}

function findDiff(a, b, pos, preferedStart) {
  var start = a.findDiffStart(b, pos)
  if (start == null) { return null }
  var ref = a.findDiffEnd(b, pos + a.size, pos + b.size);
  var endA = ref.a;
  var endB = ref.b;
  if (endA < start && a.size < b.size) {
    var move = preferedStart <= start && preferedStart >= endA ? start - preferedStart : 0
    start -= move
    endB = start + (endB - endA)
    endA = start
  } else if (endB < start) {
    var move$1 = preferedStart <= start && preferedStart >= endB ? start - preferedStart : 0
    start -= move$1
    endA = start + (endA - endB)
    endB = start
  }
  return {start: start, endA: endA, endB: endB}
}


/***/ }),
/* 24 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(2);
var joinPoint = ref.joinPoint;
var canJoin = ref.canJoin;
var findWrapping = ref.findWrapping;
var liftTarget = ref.liftTarget;
var canSplit = ref.canSplit;
var ReplaceAroundStep = ref.ReplaceAroundStep;
var ref$1 = __webpack_require__(0);
var Slice = ref$1.Slice;
var Fragment = ref$1.Fragment;
var ref$2 = __webpack_require__(1);
var Selection = ref$2.Selection;
var TextSelection = ref$2.TextSelection;
var NodeSelection = ref$2.NodeSelection;
var AllSelection = ref$2.AllSelection;

// :: (EditorState, ?(tr: Transaction)) → bool
// Delete the selection, if there is one.
function deleteSelection(state, dispatch) {
  if (state.selection.empty) { return false }
  if (dispatch) { dispatch(state.tr.deleteSelection().scrollIntoView()) }
  return true
}
exports.deleteSelection = deleteSelection

// :: (EditorState, ?(tr: Transaction), ?EditorView) → bool
// If the selection is empty and at the start of a textblock, move
// that block closer to the block before it, by lifting it out of its
// parent or, if it has no parent it doesn't share with the node
// before it, moving it into a parent of that node, or joining it with
// that. Will use the view for accurate start-of-textblock detection
// if given.
function joinBackward(state, dispatch, view) {
  var ref = state.selection;
  var $cursor = ref.$cursor;
  if (!$cursor || (view ? !view.endOfTextblock("backward", state)
                        : $cursor.parentOffset > 0))
    { return false }

  // Find the node before this one
  var before, cut, cutDepth
  for (var i = $cursor.depth - 1; !before && i >= 0; i--) { if ($cursor.index(i) > 0) {
    cut = $cursor.before(i + 1)
    before = $cursor.node(i).child($cursor.index(i) - 1)
    cutDepth = i
  } }

  // If there is no node before this, try to lift
  if (!before) {
    var range = $cursor.blockRange(), target = range && liftTarget(range)
    if (target == null) { return false }
    if (dispatch) { dispatch(state.tr.lift(range, target).scrollIntoView()) }
    return true
  }

  // If the node below has no content and the node above is
  // selectable, delete the node below and select the one above.
  if (before.isAtom && NodeSelection.isSelectable(before) && $cursor.parent.content.size == 0) {
    if (dispatch) {
      var tr = state.tr.delete(cut, cut + $cursor.parent.nodeSize)
      tr.setSelection(NodeSelection.create(tr.doc, cut - before.nodeSize))
      dispatch(tr.scrollIntoView())
    }
    return true
  }

  // If the node doesn't allow children, delete it
  if (before.isLeaf && cutDepth == $cursor.depth - 1) {
    if (dispatch) { dispatch(state.tr.delete(cut - before.nodeSize, cut).scrollIntoView()) }
    return true
  }

  // Apply the joining algorithm
  return deleteBarrier(state, cut, dispatch) || selectNextNode(state, cut, -1, dispatch)
}
exports.joinBackward = joinBackward

// :: (EditorState, ?(tr: Transaction), ?EditorView) → bool
// If the selection is empty and the cursor is at the end of a
// textblock, move the node after it closer to the node with the
// cursor (lifting it out of parents that aren't shared, moving it
// into parents of the cursor block, or joining the two when they are
// siblings). Will use the view for accurate start-of-textblock
// detection if given.
function joinForward(state, dispatch, view) {
  var ref = state.selection;
  var $cursor = ref.$cursor;
  if (!$cursor || (view ? !view.endOfTextblock("forward", state)
                        : $cursor.parentOffset < $cursor.parent.content.size))
    { return false }

  // Find the node after this one
  var after, cut, cutDepth
  for (var i = $cursor.depth - 1; !after && i >= 0; i--) {
    var parent = $cursor.node(i)
    if ($cursor.index(i) + 1 < parent.childCount) {
      after = parent.child($cursor.index(i) + 1)
      cut = $cursor.after(i + 1)
      cutDepth = i
    }
  }

  // If there is no node after this, there's nothing to do
  if (!after) { return false }

  // If the node doesn't allow children, delete it
  if (after.isLeaf && cutDepth == $cursor.depth - 1) {
    if (dispatch) { dispatch(state.tr.delete(cut, cut + after.nodeSize).scrollIntoView()) }
    return true
  }
  // Apply the joining algorithm
  return deleteBarrier(state, cut, dispatch) || selectNextNode(state, cut, 1, dispatch)
}
exports.joinForward = joinForward

// :: (EditorState, ?(tr: Transaction)) → bool
// Join the selected block or, if there is a text selection, the
// closest ancestor block of the selection that can be joined, with
// the sibling above it.
function joinUp(state, dispatch) {
  var sel = state.selection, nodeSel = sel instanceof NodeSelection, point
  if (nodeSel) {
    if (sel.node.isTextblock || !canJoin(state.doc, sel.from)) { return false }
    point = sel.from
  } else {
    point = joinPoint(state.doc, sel.from, -1)
    if (point == null) { return false }
  }
  if (dispatch) {
    var tr = state.tr.join(point)
    if (nodeSel) { tr.setSelection(NodeSelection.create(tr.doc, point - state.doc.resolve(point).nodeBefore.nodeSize)) }
    dispatch(tr.scrollIntoView())
  }
  return true
}
exports.joinUp = joinUp

// :: (EditorState, ?(tr: Transaction)) → bool
// Join the selected block, or the closest ancestor of the selection
// that can be joined, with the sibling after it.
function joinDown(state, dispatch) {
  var sel = state.selection, point
  if (sel instanceof NodeSelection) {
    if (sel.node.isTextblock || !canJoin(state.doc, sel.to)) { return false }
    point = sel.to
  } else {
    point = joinPoint(state.doc, sel.to, 1)
    if (point == null) { return false }
  }
  if (dispatch)
    { dispatch(state.tr.join(point).scrollIntoView()) }
  return true
}
exports.joinDown = joinDown

// :: (EditorState, ?(tr: Transaction)) → bool
// Lift the selected block, or the closest ancestor block of the
// selection that can be lifted, out of its parent node.
function lift(state, dispatch) {
  var ref = state.selection;
  var $from = ref.$from;
  var $to = ref.$to;
  var range = $from.blockRange($to), target = range && liftTarget(range)
  if (target == null) { return false }
  if (dispatch) { dispatch(state.tr.lift(range, target).scrollIntoView()) }
  return true
}
exports.lift = lift

// :: (EditorState, ?(tr: Transaction)) → bool
// If the selection is in a node whose type has a truthy
// [`code`](#model.NodeSpec.code) property in its spec, replace the
// selection with a newline character.
function newlineInCode(state, dispatch) {
  var ref = state.selection;
  var $head = ref.$head;
  var $anchor = ref.$anchor;
  if (!$head.parent.type.spec.code || !$head.sameParent($anchor)) { return false }
  if (dispatch) { dispatch(state.tr.insertText("\n").scrollIntoView()) }
  return true
}
exports.newlineInCode = newlineInCode

// :: (EditorState, ?(tr: Transaction)) → bool
// When the selection is in a node with a truthy
// [`code`](#model.NodeSpec.code) property in its spec, create a
// default block after the code block, and move the cursor there.
function exitCode(state, dispatch) {
  var ref = state.selection;
  var $head = ref.$head;
  var $anchor = ref.$anchor;
  if (!$head.parent.type.spec.code || !$head.sameParent($anchor)) { return false }
  var above = $head.node(-1), after = $head.indexAfter(-1), type = above.defaultContentType(after)
  if (!above.canReplaceWith(after, after, type)) { return false }
  if (dispatch) {
    var pos = $head.after(), tr = state.tr.replaceWith(pos, pos, type.createAndFill())
    tr.setSelection(Selection.near(tr.doc.resolve(pos), 1))
    dispatch(tr.scrollIntoView())
  }
  return true
}
exports.exitCode = exitCode

// :: (EditorState, ?(tr: Transaction)) → bool
// If a block node is selected, create an empty paragraph before (if
// it is its parent's first child) or after it.
function createParagraphNear(state, dispatch) {
  var ref = state.selection;
  var $from = ref.$from;
  var $to = ref.$to;
  if ($from.parent.inlineContent || $to.parent.inlineContent) { return false }
  var type = $from.parent.defaultContentType($to.indexAfter())
  if (!type || !type.isTextblock) { return false }
  if (dispatch) {
    var side = (!$from.parentOffset && $to.index() < $to.parent.childCount ? $from : $to).pos
    var tr = state.tr.insert(side, type.createAndFill())
    tr.setSelection(TextSelection.create(tr.doc, side + 1))
    dispatch(tr.scrollIntoView())
  }
  return true
}
exports.createParagraphNear = createParagraphNear

// :: (EditorState, ?(tr: Transaction)) → bool
// If the cursor is in an empty textblock that can be lifted, lift the
// block.
function liftEmptyBlock(state, dispatch) {
  var ref = state.selection;
  var $cursor = ref.$cursor;
  if (!$cursor || $cursor.parent.content.size) { return false }
  if ($cursor.depth > 1 && $cursor.after() != $cursor.end(-1)) {
    var before = $cursor.before()
    if (canSplit(state.doc, before)) {
      if (dispatch) { dispatch(state.tr.split(before).scrollIntoView()) }
      return true
    }
  }
  var range = $cursor.blockRange(), target = range && liftTarget(range)
  if (target == null) { return false }
  if (dispatch) { dispatch(state.tr.lift(range, target).scrollIntoView()) }
  return true
}
exports.liftEmptyBlock = liftEmptyBlock

// :: (EditorState, ?(tr: Transaction)) → bool
// Split the parent block of the selection. If the selection is a text
// selection, also delete its content.
function splitBlock(state, dispatch) {
  var ref = state.selection;
  var $from = ref.$from;
  var $to = ref.$to;
  if (state.selection instanceof NodeSelection && state.selection.node.isBlock) {
    if (!$from.parentOffset || !canSplit(state.doc, $from.pos)) { return false }
    if (dispatch) { dispatch(state.tr.split($from.pos).scrollIntoView()) }
    return true
  }

  if (dispatch) {
    var atEnd = $to.parentOffset == $to.parent.content.size
    var tr = state.tr
    if (state.selection instanceof TextSelection) { tr.deleteSelection() }
    var deflt = $from.depth == 0 ? null : $from.node(-1).defaultContentType($from.indexAfter(-1))
    var types = atEnd ? [{type: deflt}] : null
    var can = canSplit(tr.doc, $from.pos, 1, types)
    if (!types && !can && canSplit(tr.doc, tr.mapping.map($from.pos), 1, [{type: deflt}])) {
      types = [{type: deflt}]
      can = true
    }
    if (can) {
      tr.split(tr.mapping.map($from.pos), 1, types)
      if (!atEnd && !$from.parentOffset && $from.parent.type != deflt &&
          $from.node(-1).canReplace($from.index(-1), $from.indexAfter(-1), Fragment.from(deflt.create(), $from.parent)))
        { tr.setNodeType(tr.mapping.map($from.before()), deflt) }
    }
    dispatch(tr.scrollIntoView())
  }
  return true
}
exports.splitBlock = splitBlock

// :: (EditorState, ?(tr: Transaction)) → bool
// Acts like [`splitBlock`](#commands.splitBlock), but without
// resetting the set of active marks at the cursor.
function splitBlockKeepMarks(state, dispatch) {
  return splitBlock(state, dispatch && (function (tr) {
    var marks = state.storedMarks || (state.selection.$to.parentOffset && state.selection.$from.marks())
    if (marks) { tr.ensureMarks(marks) }
    dispatch(tr)
  }))
}
exports.splitBlockKeepMarks = splitBlockKeepMarks

// :: (EditorState, ?(tr: Transaction)) → bool
// Move the selection to the node wrapping the current selection, if
// any. (Will not select the document node.)
function selectParentNode(state, dispatch) {
  var ref = state.selection;
  var $from = ref.$from;
  var to = ref.to;
  var pos
  var same = $from.sharedDepth(to)
  if (same == 0) { return false }
  pos = $from.before(same)
  if (dispatch) { dispatch(state.tr.setSelection(NodeSelection.create(state.doc, pos))) }
  return true
}
exports.selectParentNode = selectParentNode

// :: (EditorState, ?(tr: Transaction)) → bool
// Select the whole document.
function selectAll(state, dispatch) {
  if (dispatch) { dispatch(state.tr.setSelection(new AllSelection(state.doc))) }
  return true
}
exports.selectAll = selectAll

function joinMaybeClear(state, $pos, dispatch) {
  var before = $pos.nodeBefore, after = $pos.nodeAfter, index = $pos.index()
  if (!before || !after || !before.type.compatibleContent(after.type)) { return false }
  if (!before.content.size && $pos.parent.canReplace(index - 1, index)) {
    if (dispatch) { dispatch(state.tr.delete($pos.pos - before.nodeSize, $pos.pos).scrollIntoView()) }
    return true
  }
  if (!$pos.parent.canReplace(index, index + 1) || !(after.isTextblock || canJoin(state.doc, $pos.pos)))
    { return false }
  if (dispatch)
    { dispatch(state.tr
             .clearNonMatching($pos.pos, before.contentMatchAt(before.childCount))
             .join($pos.pos)
             .scrollIntoView()) }
  return true
}

function deleteBarrier(state, cut, dispatch) {
  var $cut = state.doc.resolve(cut), before = $cut.nodeBefore, after = $cut.nodeAfter, conn, match
  if (joinMaybeClear(state, $cut, dispatch)) { return true }

  if ($cut.parent.canReplace($cut.index(), $cut.index() + 1) &&
      (conn = (match = before.contentMatchAt(before.childCount)).findWrappingFor(after))&&
      match.matchType((conn[0] || after).type, (conn[0] || after).attrs).validEnd()) {
    if (dispatch) {
      var end = cut + after.nodeSize, wrap = Fragment.empty
      for (var i = conn.length - 1; i >= 0; i--)
        { wrap = Fragment.from(conn[i].type.create(conn[i].attrs, wrap)) }
      wrap = Fragment.from(before.copy(wrap))
      var tr = state.tr.step(new ReplaceAroundStep(cut - 1, end, cut, end, new Slice(wrap, 1, 0), conn.length, true))
      var joinAt = end + 2 * conn.length
      if (canJoin(tr.doc, joinAt)) { tr.join(joinAt) }
      dispatch(tr.scrollIntoView())
    }
    return true
  }

  var selAfter = Selection.findFrom($cut, 1)
  var range = selAfter && selAfter.$from.blockRange(selAfter.$to), target = range && liftTarget(range)
  if (target != null && target >= $cut.depth) {
    if (dispatch) { dispatch(state.tr.lift(range, target).scrollIntoView()) }
    return true
  }

  return false
}

function selectNextNode(state, cut, dir, dispatch) {
  var $cut = state.doc.resolve(cut)
  var node = dir > 0 ? $cut.nodeAfter : $cut.nodeBefore
  if (!node || !NodeSelection.isSelectable(node)) { return false }
  if (dispatch)
    { dispatch(state.tr.setSelection(NodeSelection.create(state.doc, cut - (dir > 0 ? 0 : node.nodeSize))).scrollIntoView()) }
  return true
}

// Parameterized commands

// :: (NodeType, ?Object) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Wrap the selection in a node of the given type with the given
// attributes.
function wrapIn(nodeType, attrs) {
  return function(state, dispatch) {
    var ref = state.selection;
    var $from = ref.$from;
    var $to = ref.$to;
    var range = $from.blockRange($to), wrapping = range && findWrapping(range, nodeType, attrs)
    if (!wrapping) { return false }
    if (dispatch) { dispatch(state.tr.wrap(range, wrapping).scrollIntoView()) }
    return true
  }
}
exports.wrapIn = wrapIn

// :: (NodeType, ?Object) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Returns a command that tries to set the textblock around the
// selection to the given node type with the given attributes.
function setBlockType(nodeType, attrs) {
  return function(state, dispatch) {
    var ref = state.selection;
    var $from = ref.$from;
    var $to = ref.$to;
    var depth, target
    if (state.selection instanceof NodeSelection) {
      depth = $from.depth
      target = state.selection.node
    } else {
      if (!$from.depth || $to.pos > $from.end()) { return false }
      depth = $from.depth - 1
      target = $from.parent
    }
    if (!target.isTextblock || target.hasMarkup(nodeType, attrs)) { return false }
    var index = $from.index(depth)
    if (!$from.node(depth).canReplaceWith(index, index + 1, nodeType)) { return false }
    if (dispatch) {
      var where = $from.before(depth + 1)
      dispatch(state.tr
               .clearNonMatching(where, nodeType.contentExpr.start(attrs))
               .setNodeType(where, nodeType, attrs)
               .scrollIntoView())
    }
    return true
  }
}
exports.setBlockType = setBlockType

function markApplies(doc, ranges, type) {
  var loop = function ( i ) {
    var ref = ranges[i];
    var $from = ref.$from;
    var $to = ref.$to;
    var can = $from.depth == 0 ? doc.contentMatchAt(0).allowsMark(type) : false
    doc.nodesBetween($from.pos, $to.pos, function (node) {
      if (can) { return false }
      can = node.inlineContent && node.contentMatchAt(0).allowsMark(type)
    })
    if (can) { return { v: true } }
  };

  for (var i = 0; i < ranges.length; i++) {
    var returned = loop( i );

    if ( returned ) return returned.v;
  }
  return false
}

// :: (MarkType, ?Object) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Create a command function that toggles the given mark with the
// given attributes. Will return `false` when the current selection
// doesn't support that mark. This will remove the mark if any marks
// of that type exist in the selection, or add it otherwise. If the
// selection is empty, this applies to the [stored
// marks](#state.EditorState.storedMarks) instead of a range of the
// document.
function toggleMark(markType, attrs) {
  return function(state, dispatch) {
    var ref = state.selection;
    var empty = ref.empty;
    var $cursor = ref.$cursor;
    var ranges = ref.ranges;
    if ((empty && !$cursor) || !markApplies(state.doc, ranges, markType)) { return false }
    if (dispatch) {
      if ($cursor) {
        if (markType.isInSet(state.storedMarks || $cursor.marks()))
          { dispatch(state.tr.removeStoredMark(markType)) }
        else
          { dispatch(state.tr.addStoredMark(markType.create(attrs))) }
      } else {
        var has = false, tr = state.tr
        for (var i = 0; !has && i < ranges.length; i++) {
          var ref$1 = ranges[i];
          var $from = ref$1.$from;
          var $to = ref$1.$to;
          has = state.doc.rangeHasMark($from.pos, $to.pos, markType)
        }
        for (var i$1 = 0; i$1 < ranges.length; i$1++) {
          var ref$2 = ranges[i$1];
          var $from$1 = ref$2.$from;
          var $to$1 = ref$2.$to;
          if (has) { tr.removeMark($from$1.pos, $to$1.pos, markType) }
          else { tr.addMark($from$1.pos, $to$1.pos, markType.create(attrs)) }
        }
        dispatch(tr.scrollIntoView())
      }
    }
    return true
  }
}
exports.toggleMark = toggleMark

function wrapDispatchForJoin(dispatch, isJoinable) {
  return function (tr) {
    if (!tr.isGeneric) { return dispatch(tr) }

    var ranges = []
    for (var i = 0; i < tr.mapping.maps.length; i++) {
      var map = tr.mapping.maps[i]
      for (var j = 0; j < ranges.length; j++)
        { ranges[j] = map.map(ranges[j]) }
      map.forEach(function (_s, _e, from, to) { return ranges.push(from, to); })
    }

    // Figure out which joinable points exist inside those ranges,
    // by checking all node boundaries in their parent nodes.
    var joinable = []
    for (var i$1 = 0; i$1 < ranges.length; i$1 += 2) {
      var from = ranges[i$1], to = ranges[i$1 + 1]
      var $from = tr.doc.resolve(from), depth = $from.sharedDepth(to), parent = $from.node(depth)
      for (var index = $from.indexAfter(depth), pos = $from.after(depth + 1); pos <= to; ++index) {
        var after = parent.maybeChild(index)
        if (!after) { break }
        if (index && joinable.indexOf(pos) == -1) {
          var before = parent.child(index - 1)
          if (before.type == after.type && isJoinable(before, after))
            { joinable.push(pos) }
        }
        pos += after.nodeSize
      }
    }
    // Join the joinable points
    joinable.sort(function (a, b) { return a - b; })
    for (var i$2 = joinable.length - 1; i$2 >= 0; i$2--) {
      if (canJoin(tr.doc, joinable[i$2])) { tr.join(joinable[i$2]) }
    }
    dispatch(tr)
  }
}

// :: ((state: EditorState, ?(tr: Transaction)) → bool, union<(before: Node, after: Node) → bool, [string]>) → (state: EditorState, ?(tr: Transaction)) → bool
// Wrap a command so that, when it produces a transform that causes
// two joinable nodes to end up next to each other, those are joined.
// Nodes are considered joinable when they are of the same type and
// when the `isJoinable` predicate returns true for them or, if an
// array of strings was passed, if their node type name is in that
// array.
function autoJoin(command, isJoinable) {
  if (Array.isArray(isJoinable)) {
    var types = isJoinable
    isJoinable = function (node) { return types.indexOf(node.type.name) > -1; }
  }
  return function (state, dispatch) { return command(state, dispatch && wrapDispatchForJoin(dispatch, isJoinable)); }
}
exports.autoJoin = autoJoin

// :: (...[(EditorState, ?(tr: Transaction)) → bool]) → (EditorState, ?(tr: Transaction)) → bool
// Combine a number of command functions into a single function (which
// calls them one by one until one returns true).
function chainCommands() {
  var commands = [], len = arguments.length;
  while ( len-- ) commands[ len ] = arguments[ len ];

  return function(state, dispatch, view) {
    for (var i = 0; i < commands.length; i++)
      { if (commands[i](state, dispatch, view)) { return true } }
    return false
  }
}
exports.chainCommands = chainCommands

// :: Object
// A basic keymap containing bindings not specific to any schema.
// Binds the following keys (when multiple commands are listed, they
// are chained with [`chainCommands`](#commands.chainCommands):
//
// * **Enter** to `newlineInCode`, `createParagraphNear`, `liftEmptyBlock`, `splitBlock`
// * **Mod-Enter** to `exitCode`
// * **Backspace** to `deleteSelection`, `joinBackward`
// * **Mod-Backspace** to `deleteSelection`, `joinBackward`
// * **Delete** to `deleteSelection`, `joinForward`
// * **Mod-Delete** to `deleteSelection`, `joinForward`
// * **Alt-ArrowUp** to `joinUp`
// * **Alt-ArrowDown** to `joinDown`
// * **Mod-BracketLeft** to `lift`
// * **Escape** to `selectParentNode`
var baseKeymap = {
  "Enter": chainCommands(newlineInCode, createParagraphNear, liftEmptyBlock, splitBlock),
  "Mod-Enter": exitCode,

  "Backspace": chainCommands(deleteSelection, joinBackward),
  "Mod-Backspace": chainCommands(deleteSelection, joinBackward),
  "Delete": chainCommands(deleteSelection, joinForward),
  "Mod-Delete": chainCommands(deleteSelection, joinForward),

  "Alt-ArrowUp": joinUp,
  "Alt-ArrowDown": joinDown,
  "Mod-BracketLeft": lift,
  "Escape": selectParentNode,
  "Mod-a": selectAll
}

// declare global: os, navigator
var mac = typeof navigator != "undefined" ? /Mac/.test(navigator.platform)
          : typeof os != "undefined" ? os.platform() == "darwin" : false

if (mac) {
  var extra = {
    "Ctrl-h": baseKeymap["Backspace"],
    "Alt-Backspace": baseKeymap["Mod-Backspace"],
    "Ctrl-d": baseKeymap["Delete"],
    "Ctrl-Alt-Backspace": baseKeymap["Mod-Delete"],
    "Alt-Delete": baseKeymap["Mod-Delete"],
    "Alt-d": baseKeymap["Mod-Delete"]
  }
  for (var prop in extra) { baseKeymap[prop] = extra[prop] }
}

exports.baseKeymap = baseKeymap


/***/ }),
/* 25 */
/***/ (function(module, exports, __webpack_require__) {

var RopeSequence = __webpack_require__(48)
var ref = __webpack_require__(2);
var Mapping = ref.Mapping;
var ref$1 = __webpack_require__(1);
var Plugin = ref$1.Plugin;
var PluginKey = ref$1.PluginKey;

// ProseMirror's history isn't simply a way to roll back to a previous
// state, because ProseMirror supports applying changes without adding
// them to the history (for example during collaboration).
//
// To this end, each 'Branch' (one for the undo history and one for
// the redo history) keeps an array of 'Items', which can optionally
// hold a step (an actual undoable change), and always hold a position
// map (which is needed to move changes below them to apply to the
// current document).
//
// An item that has both a step and a selection bookmark is the start
// of an 'event' — a group of changes that will be undone or redone at
// once. (It stores only the bookmark, since that way we don't have to
// provide a document until the selection is actually applied, which
// is useful when compressing.)

// Used to schedule history compression
var max_empty_items = 500

var Branch = function Branch(items, eventCount) {
  this.items = items
  this.eventCount = eventCount
};

// : (Node, bool, ?Item) → ?{transform: Transform, selection: Object}
// Pop the latest event off the branch's history and apply it
// to a document transform.
Branch.prototype.popEvent = function popEvent (state, preserveItems) {
    var this$1 = this;

  if (this.eventCount == 0) { return null }

  var end = this.items.length
  for (;; end--) {
    var next = this$1.items.get(end - 1)
    if (next.selection) { --end; break }
  }

  var remap, mapFrom
  if (preserveItems) {
    remap = this.remapping(end, this.items.length)
    mapFrom = remap.maps.length
  }
  var transform = state.tr
  var selection, remaining
  var addAfter = [], addBefore = []

  this.items.forEach(function (item, i) {
    if (!item.step) {
      if (!remap) {
        remap = this$1.remapping(end, i + 1)
        mapFrom = remap.maps.length
      }
      mapFrom--
      addBefore.push(item)
      return
    }

    if (remap) {
      addBefore.push(new Item(item.map))
      var step = item.step.map(remap.slice(mapFrom)), map

      if (step && transform.maybeStep(step).doc) {
        map = transform.mapping.maps[transform.mapping.maps.length - 1]
        addAfter.push(new Item(map, null, null, addAfter.length + addBefore.length))
      }
      mapFrom--
      if (map) { remap.appendMap(map, mapFrom) }
    } else {
      transform.maybeStep(item.step)
    }

    if (item.selection) {
      selection = remap ? item.selection.map(remap.slice(mapFrom)) : item.selection
      remaining = new Branch(this$1.items.slice(0, end).append(addBefore.reverse().concat(addAfter)), this$1.eventCount - 1)
      return false
    }
  }, this.items.length, 0)

  return {remaining: remaining, transform: transform, selection: selection}
};

// : (Transform, Selection, Object)
// Create a new branch with the given transform added.
Branch.prototype.addTransform = function addTransform (transform, selection, histOptions) {
  var newItems = [], eventCount = this.eventCount + (selection ? 1 : 0)
  var oldItems = this.items, lastItem = !histOptions.preserveItems && oldItems.length ? oldItems.get(oldItems.length - 1) : null

  for (var i = 0; i < transform.steps.length; i++) {
    var step = transform.steps[i].invert(transform.docs[i])
    var item = new Item(transform.mapping.maps[i], step, selection), merged = (void 0)
    if (merged = lastItem && lastItem.merge(item)) {
      item = merged
      if (i) { newItems.pop() }
      else { oldItems = oldItems.slice(0, oldItems.length - 1) }
    }
    newItems.push(item)
    selection = null
    if (!histOptions.preserveItems) { lastItem = item }
  }
  var overflow = eventCount - histOptions.depth
  if (overflow > DEPTH_OVERFLOW) {
    oldItems = cutOffEvents(oldItems, overflow)
    eventCount -= overflow
  }
  return new Branch(oldItems.append(newItems), eventCount)
};

Branch.prototype.remapping = function remapping (from, to) {
  var maps = [], mirrors = []
  this.items.forEach(function (item, i) {
    if (item.mirrorOffset != null) {
      var mirrorPos = i - item.mirrorOffset
      if (mirrorPos >= from) { mirrors.push(maps.length - item.mirrorOffset, maps.length) }
    }
    maps.push(item.map)
  }, from, to)
  return new Mapping(maps, mirrors)
};

Branch.prototype.addMaps = function addMaps (array) {
  if (this.eventCount == 0) { return this }
  return new Branch(this.items.append(array.map(function (map) { return new Item(map); })), this.eventCount)
};

// : ([StepMap], Transform, [number])
// When the collab module receives remote changes, the history has
// to know about those, so that it can adjust the steps that were
// rebased on top of the remote changes, and include the position
// maps for the remote changes in its array of items.
Branch.prototype.rebased = function rebased (rebasedTransform, rebasedCount) {
  if (!this.eventCount) { return this }

  var rebasedItems = [], start = Math.max(0, this.items.length - rebasedCount)

  var mapping = rebasedTransform.mapping
  var newUntil = rebasedTransform.steps.length
  var eventCount = this.eventCount

  var iRebased = rebasedCount
  this.items.forEach(function (item) {
    var pos = mapping.getMirror(--iRebased)
    if (pos == null) { return }
    newUntil = Math.min(newUntil, pos)
    var map = mapping.maps[pos]
    if (item.step) {
      var step = rebasedTransform.steps[pos].invert(rebasedTransform.docs[pos])
      var selection = item.selection && item.selection.map(mapping.slice(iRebased, pos))
      rebasedItems.push(new Item(map, step, selection))
    } else {
      if (item.selection) { eventCount-- }
      rebasedItems.push(new Item(map))
    }
  }, start)

  var newMaps = []
  for (var i = rebasedCount; i < newUntil; i++)
    { newMaps.push(new Item(mapping.maps[i])) }
  var items = this.items.slice(0, start).append(newMaps).append(rebasedItems)
  var branch = new Branch(items, eventCount)
  if (branch.emptyItemCount() > max_empty_items)
    { branch = branch.compress(this.items.length - rebasedItems.length) }
  return branch
};

Branch.prototype.emptyItemCount = function emptyItemCount () {
  var count = 0
  this.items.forEach(function (item) { if (!item.step) { count++ } })
  return count
};

// Compressing a branch means rewriting it to push the air (map-only
// items) out. During collaboration, these naturally accumulate
// because each remote change adds one. The `upto` argument is used
// to ensure that only the items below a given level are compressed,
// because `rebased` relies on a clean, untouched set of items in
// order to associate old items with rebased steps.
Branch.prototype.compress = function compress (upto) {
    if ( upto === void 0 ) upto = this.items.length;

  var remap = this.remapping(0, upto), mapFrom = remap.maps.length
  var items = [], events = 0
  this.items.forEach(function (item, i) {
    if (i >= upto) {
      items.push(item)
      if (item.selection) { events++ }
    } else if (item.step) {
      var step = item.step.map(remap.slice(mapFrom)), map = step && step.getMap()
      mapFrom--
      if (map) { remap.appendMap(map, mapFrom) }
      if (step) {
        var selection = item.selection && item.selection.map(remap.slice(mapFrom))
        if (selection) { events++ }
        var newItem = new Item(map.invert(), step, selection), merged, last = items.length - 1
        if (merged = items.length && items[last].merge(newItem))
          { items[last] = merged }
        else
          { items.push(newItem) }
      }
    } else if (item.map) {
      mapFrom--
    }
  }, this.items.length, 0)
  return new Branch(RopeSequence.from(items.reverse()), events)
};

Branch.empty = new Branch(RopeSequence.empty, 0)

function cutOffEvents(items, n) {
  var cutPoint
  items.forEach(function (item, i) {
    if (item.selection && (n-- == 0)) {
      cutPoint = i
      return false
    }
  })
  return items.slice(cutPoint)
}

var Item = function Item(map, step, selection, mirrorOffset) {
  this.map = map
  this.step = step
  this.selection = selection
  this.mirrorOffset = mirrorOffset
};

Item.prototype.merge = function merge (other) {
  if (this.step && other.step && !other.selection) {
    var step = other.step.merge(this.step)
    if (step) { return new Item(step.getMap().invert(), step, this.selection) }
  }
};

// The value of the state field that tracks undo/redo history for that
// state. Will be stored in the plugin state when the history plugin
// is active.
var HistoryState = function HistoryState(done, undone, prevMap, prevTime) {
  this.done = done
  this.undone = undone
  this.prevMap = prevMap
  this.prevTime = prevTime
};
exports.HistoryState = HistoryState

var DEPTH_OVERFLOW = 20

// : (EditorState, Transform, Selection, Object)
// Record a transformation in undo history.
function applyTransaction(history, selection, tr, options) {
  var newState = tr.getMeta(historyKey), rebased
  if (newState) { return newState }

  if (tr.getMeta(closeHistoryKey)) { history = new HistoryState(history.done, history.undone, null, 0) }

  var appended = tr.getMeta("appendedTransaction")
  if (tr.steps.length == 0) {
    return history
  } else if ((appended || tr).getMeta("addToHistory") !== false) {
    // Group transforms that occur in quick succession into one event.
    var newGroup = history.prevTime < (tr.time || 0) - options.newGroupDelay ||
        !appended && !isAdjacentToLastStep(tr, history.prevMap, history.done)
    return new HistoryState(history.done.addTransform(tr, newGroup ? selection.getBookmark() : null, options),
                            Branch.empty, tr.mapping.maps[tr.steps.length - 1], tr.time)
  } else if (rebased = tr.getMeta("rebased")) {
    // Used by the collab module to tell the history that some of its
    // content has been rebased.
    return new HistoryState(history.done.rebased(tr, rebased),
                            history.undone.rebased(tr, rebased),
                            history.prevMap && tr.mapping.maps[tr.steps.length - 1], history.prevTime)
  } else {
    return new HistoryState(history.done.addMaps(tr.mapping.maps),
                            history.undone.addMaps(tr.mapping.maps),
                            history.prevMap, history.prevTime)
  }
}

function isAdjacentToLastStep(transform, prevMap, done) {
  if (!prevMap) { return false }
  var firstMap = transform.mapping.maps[0], adjacent = false
  if (!firstMap) { return true }
  firstMap.forEach(function (start, end) {
    done.items.forEach(function (item) {
      if (item.step) {
        prevMap.forEach(function (_start, _end, rStart, rEnd) {
          if (start <= rEnd && end >= rStart) { adjacent = true }
        })
        return false
      } else {
        start = item.map.invert().map(start, -1)
        end = item.map.invert().map(end, 1)
      }
    }, done.items.length, 0)
  })
  return adjacent
}

// : (HistoryState, EditorState, (tr: Transaction), bool)
// Apply the latest event from one branch to the document and optionally
// shift the event onto the other branch. Returns true when an event could
// be shifted.
function histTransaction(history, state, dispatch, redo) {
  var histOptions = historyKey.get(state).spec.config
  var pop = (redo ? history.undone : history.done).popEvent(state, histOptions.preserveItems)
  if (!pop) { return }

  var selectionBefore = state.selection
  var selection = pop.selection.resolve(pop.transform.doc)
  var added = (redo ? history.done : history.undone).addTransform(pop.transform, selectionBefore.getBookmark(), histOptions)

  var newHist = new HistoryState(redo ? added : pop.remaining, redo ? pop.remaining : added, null, 0)
  dispatch(pop.transform.setSelection(selection).setMeta(historyKey, newHist).scrollIntoView())
}

// :: (Transaction) → Transaction
// Set a flag on the given transaction that will prevent further steps
// from being appended to an existing history event (so that they
// require a separate undo command to undo).
function closeHistory(tr) {
  return tr.setMeta(closeHistoryKey, true)
}
exports.closeHistory = closeHistory

var historyKey = new PluginKey("history")
var closeHistoryKey = new PluginKey("closeHistory")

// :: (?Object) → Plugin
// Returns a plugin that enables the undo history for an editor. The
// plugin will track undo and redo stacks, which the
// [`undo`](##history.undo) and [`redo`](##history.redo) commands can
// use to move the state back and forward.
//
// Note that this implementation doesn't implement history by simply
// resetting back to some previous state. In order to support
// collaborative editing (as well as some other use cases), it
// selectively rolls back some transactions, but not other (for
// example, not the changes made by other users). You can set an
// `"addToHistory"` [metadata property](##state.Transaction.setMeta)
// of `false` on a transaction to prevent it from being rolled back by
// undo.
//
//   config::-
//   Supports the following configuration options:
//
//     depth:: ?number
//     The amount of history events that are collected before the
//     oldest events are discarded. Defaults to 100.
//
//     newGroupDelay:: number
//     The delay between changes after which a new group should be
//     started. Defaults to 500 (milliseconds). Note that when changes
//     aren't adjacent, a new group is always started.
//
//     preserveItems:: ?bool
//     Whether to preserve the steps exactly as they came in. **Must**
//     be true when using the history together with the collaborative
//     editing plugin, to allow syncing the history when concurrent
//     changes come in. Defaults to false.
function history(config) {
  config = {depth: config && config.depth || 100,
            preserveItems: !!(config && config.preserveItems),
            newGroupDelay: config && config.newGroupDelay || 500}
  return new Plugin({
    key: historyKey,

    state: {
      init: function init() {
        return new HistoryState(Branch.empty, Branch.empty, null, 0)
      },
      apply: function apply(tr, hist, state) {
        return applyTransaction(hist, state.selection, tr, config)
      }
    },

    config: config
  })
}
exports.history = history

// :: (EditorState, ?(tr: Transaction)) → bool
// A command function that undoes the last change, if any.
function undo(state, dispatch) {
  var hist = historyKey.getState(state)
  if (!hist || hist.done.eventCount == 0) { return false }
  if (dispatch) { histTransaction(hist, state, dispatch, false) }
  return true
}
exports.undo = undo

// :: (EditorState, ?(tr: Transaction)) → bool
// A command function that redoes the last undone change, if any.
function redo(state, dispatch) {
  var hist = historyKey.getState(state)
  if (!hist || hist.undone.eventCount == 0) { return false }
  if (dispatch) { histTransaction(hist, state, dispatch, true) }
  return true
}
exports.redo = redo

// :: (EditorState) → number
// The amount of undoable events available in a given state.
function undoDepth(state) {
  var hist = historyKey.getState(state)
  return hist ? hist.done.eventCount : 0
}
exports.undoDepth = undoDepth

// :: (EditorState) → number
// The amount of redoable events available in a given editor state.
function redoDepth(state) {
  var hist = historyKey.getState(state)
  return hist ? hist.undone.eventCount : 0
}
exports.redoDepth = redoDepth


/***/ }),
/* 26 */
/***/ (function(module, exports, __webpack_require__) {

;var assign;
((assign = __webpack_require__(10), exports.InputRule = assign.InputRule, exports.inputRules = assign.inputRules, exports.undoInputRule = assign.undoInputRule, assign))
;var assign$1;
((assign$1 = __webpack_require__(31), exports.emDash = assign$1.emDash, exports.ellipsis = assign$1.ellipsis, exports.openDoubleQuote = assign$1.openDoubleQuote, exports.closeDoubleQuote = assign$1.closeDoubleQuote, exports.openSingleQuote = assign$1.openSingleQuote, exports.closeSingleQuote = assign$1.closeSingleQuote, exports.smartQuotes = assign$1.smartQuotes, exports.allInputRules = assign$1.allInputRules, assign$1))
;var assign$2;
((assign$2 = __webpack_require__(32), exports.wrappingInputRule = assign$2.wrappingInputRule, exports.textblockTypeInputRule = assign$2.textblockTypeInputRule, exports.blockQuoteRule = assign$2.blockQuoteRule, exports.orderedListRule = assign$2.orderedListRule, exports.bulletListRule = assign$2.bulletListRule, exports.codeBlockRule = assign$2.codeBlockRule, exports.headingRule = assign$2.headingRule, assign$2))


/***/ }),
/* 27 */
/***/ (function(module, exports, __webpack_require__) {

var keyName = __webpack_require__(49)
var ref = __webpack_require__(1);
var Plugin = ref.Plugin;

// declare global: navigator

var mac = typeof navigator != "undefined" ? /Mac/.test(navigator.platform) : false

function normalizeKeyName(name) {
  var parts = name.split(/-(?!$)/), result = parts[parts.length - 1]
  if (result == "Space") { result = " " }
  var alt, ctrl, shift, meta
  for (var i = 0; i < parts.length - 1; i++) {
    var mod = parts[i]
    if (/^(cmd|meta|m)$/i.test(mod)) { meta = true }
    else if (/^a(lt)?$/i.test(mod)) { alt = true }
    else if (/^(c|ctrl|control)$/i.test(mod)) { ctrl = true }
    else if (/^s(hift)?$/i.test(mod)) { shift = true }
    else if (/^mod$/i.test(mod)) { if (mac) { meta = true; } else { ctrl = true } }
    else { throw new Error("Unrecognized modifier name: " + mod) }
  }
  if (alt) { result = "Alt-" + result }
  if (ctrl) { result = "Ctrl-" + result }
  if (meta) { result = "Meta-" + result }
  if (shift) { result = "Shift-" + result }
  return result
}

function normalize(map) {
  var copy = Object.create(null)
  for (var prop in map) { copy[normalizeKeyName(prop)] = map[prop] }
  return copy
}

function modifiers(name, event, shift) {
  if (event.altKey) { name = "Alt-" + name }
  if (event.ctrlKey) { name = "Ctrl-" + name }
  if (event.metaKey) { name = "Meta-" + name }
  if (shift !== false && event.shiftKey) { name = "Shift-" + name }
  return name
}

// :: (Object) → Plugin
// Create a keymap plugin for the given set of bindings.
//
// Bindings should map key names to [command](#commands)-style
// functions, which will be called with `(EditorState, dispatch,
// EditorView)` arguments, and should return true when they've handled
// the key. Note that the view argument isn't part of the command
// protocol, but can be used as an escape hatch if a binding needs to
// directly interact with the UI.
//
// Key names may be strings like `"Shift-Ctrl-Enter"`, a key
// identifier prefixed with zero or more modifiers. Key identifiers
// are based on the strings that can appear in
// [`KeyEvent.key`](https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key).
// Use lowercase letters to refer to letter keys (or uppercase letters
// if you want shift to be held). You may use `"Space"` as an alias
// for the `" "` name.
//
// Modifiers can be given in any order. `Shift-` (or `s-`), `Alt-` (or
// `a-`), `Ctrl-` (or `c-` or `Control-`) and `Cmd-` (or `m-` or
// `Meta-`) are recognized. For characters that are created by holding
// shift, the `Shift-` prefix is implied, and should not be added
// explicitly.
//
// You can use `Mod-` as a shorthand for `Cmd-` on Mac and `Ctrl-` on
// other platforms.
//
// You can add multiple keymap plugins to an editor. The order in
// which they appear determines their precedence (the ones early in
// the array get to dispatch first).
function keymap(bindings) {
  return new Plugin({props: {handleKeyDown: keydownHandler(bindings)}})
}
exports.keymap = keymap

// :: (Object) → (view: EditorView, event: dom.Event) → bool
// Given a keymap, return a [keydown
// handler](#view.EditorProps.handleKeyDown) that implements the
// bindings for that map, using the same rules as
// [`keymap`](##keymap.keymap).
function keydownHandler(bindings) {
  var map = normalize(bindings)
  return function(view, event) {
    var name = keyName(event), isChar = name.length == 1 && name != " ", baseName
    var direct = map[modifiers(name, event, !isChar)]
    if (direct && direct(view.state, view.dispatch, view)) { return true }
    if (event.shiftKey && isChar && (baseName = keyName.base[event.keyCode])) {
      var withShift = map[modifiers(baseName, event, true)]
      if (withShift && withShift(view.state, view.dispatch, view)) { return true }
    }
    return false
  }
}
exports.keydownHandler = keydownHandler


/***/ }),
/* 28 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(2);
var findWrapping = ref.findWrapping;
var liftTarget = ref.liftTarget;
var canSplit = ref.canSplit;
var ReplaceAroundStep = ref.ReplaceAroundStep;
var ref$1 = __webpack_require__(0);
var Slice = ref$1.Slice;
var Fragment = ref$1.Fragment;
var NodeRange = ref$1.NodeRange;

// :: NodeSpec
// An ordered list node type spec. Has a single attribute, `order`,
// which determines the number at which the list starts counting, and
// defaults to 1.
var orderedList = {
  attrs: {order: {default: 1}},
  parseDOM: [{tag: "ol", getAttrs: function getAttrs(dom) {
    return {order: dom.hasAttribute("start") ? +dom.getAttribute("start") : 1}
  }}],
  toDOM: function toDOM(node) {
    return ["ol", {start: node.attrs.order == 1 ? null : node.attrs.order}, 0]
  }
}
exports.orderedList = orderedList

// :: NodeSpec
// A bullet list node spec.
var bulletList = {
  parseDOM: [{tag: "ul"}],
  toDOM: function toDOM() { return ["ul", 0] }
}
exports.bulletList = bulletList

// :: NodeSpec
// A list item node spec.
var listItem = {
  parseDOM: [{tag: "li"}],
  toDOM: function toDOM() { return ["li", 0] },
  defining: true
}
exports.listItem = listItem

function add(obj, props) {
  var copy = {}
  for (var prop in obj) { copy[prop] = obj[prop] }
  for (var prop$1 in props) { copy[prop$1] = props[prop$1] }
  return copy
}

// :: (OrderedMap, string, ?string) → OrderedMap
// Convenience function for adding list-related node types to a map
// describing the nodes in a schema. Adds `OrderedList` as
// `"ordered_list"`, `BulletList` as `"bullet_list"`, and `ListItem`
// as `"list_item"`. `itemContent` determines the content expression
// for the list items. If you want the commands defined in this module
// to apply to your list structure, it should have a shape like
// `"paragraph block*"`, a plain textblock type followed by zero or
// more arbitrary nodes. `listGroup` can be given to assign a group
// name to the list node types, for example `"block"`.
function addListNodes(nodes, itemContent, listGroup) {
  return nodes.append({
    ordered_list: add(orderedList, {content: "list_item+", group: listGroup}),
    bullet_list: add(bulletList, {content: "list_item+", group: listGroup}),
    list_item: add(listItem, {content: itemContent})
  })
}
exports.addListNodes = addListNodes

// :: (NodeType, ?Object) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Returns a command function that wraps the selection in a list with
// the given type an attributes. If `apply` is `false`, only return a
// value to indicate whether this is possible, but don't actually
// perform the change.
function wrapInList(listType, attrs) {
  return function(state, dispatch) {
    var ref = state.selection;
    var $from = ref.$from;
    var $to = ref.$to;
    var range = $from.blockRange($to), doJoin = false, outerRange = range
    if (!range) { return false }
    // This is at the top of an existing list item
    if (range.depth >= 2 && $from.node(range.depth - 1).type.compatibleContent(listType) && range.startIndex == 0) {
      // Don't do anything if this is the top of the list
      if ($from.index(range.depth - 1) == 0) { return false }
      var $insert = state.doc.resolve(range.start - 2)
      outerRange = new NodeRange($insert, $insert, range.depth)
      if (range.endIndex < range.parent.childCount)
        { range = new NodeRange($from, state.doc.resolve($to.end(range.depth)), range.depth) }
      doJoin = true
    }
    var wrap = findWrapping(outerRange, listType, attrs, range)
    if (!wrap) { return false }
    if (dispatch) { dispatch(doWrapInList(state.tr, range, wrap, doJoin, listType).scrollIntoView()) }
    return true
  }
}
exports.wrapInList = wrapInList

function doWrapInList(tr, range, wrappers, joinBefore, listType) {
  var content = Fragment.empty
  for (var i = wrappers.length - 1; i >= 0; i--)
    { content = Fragment.from(wrappers[i].type.create(wrappers[i].attrs, content)) }

  tr.step(new ReplaceAroundStep(range.start - (joinBefore ? 2 : 0), range.end, range.start, range.end,
                                new Slice(content, 0, 0), wrappers.length, true))

  var found = 0
  for (var i$1 = 0; i$1 < wrappers.length; i$1++) { if (wrappers[i$1].type == listType) { found = i$1 + 1 } }
  var splitDepth = wrappers.length - found

  var splitPos = range.start + wrappers.length - (joinBefore ? 2 : 0), parent = range.parent
  for (var i$2 = range.startIndex, e = range.endIndex, first = true; i$2 < e; i$2++, first = false) {
    if (!first && canSplit(tr.doc, splitPos, splitDepth)) { tr.split(splitPos, splitDepth) }
    splitPos += parent.child(i$2).nodeSize + (first ? 0 : 2 * splitDepth)
  }
  return tr
}

// :: (NodeType) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Build a command that splits a non-empty textblock at the top level
// of a list item by also splitting that list item.
function splitListItem(itemType) {
  return function(state, dispatch) {
    var ref = state.selection;
    var $from = ref.$from;
    var $to = ref.$to;
    var node = ref.node;
    if ((node && node.isBlock) || !$from.parent.content.size ||
        $from.depth < 2 || !$from.sameParent($to)) { return false }
    var grandParent = $from.node(-1)
    if (grandParent.type != itemType) { return false }
    var nextType = $to.pos == $from.end() ? grandParent.defaultContentType($from.indexAfter(-1)) : null
    var tr = state.tr.delete($from.pos, $to.pos)
    var types = nextType && [null, {type: nextType}]
    if (!canSplit(tr.doc, $from.pos, 2, types)) { return false }
    if (dispatch) { dispatch(tr.split($from.pos, 2, types).scrollIntoView()) }
    return true
  }
}
exports.splitListItem = splitListItem

// :: (NodeType) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Create a command to lift the list item around the selection up into
// a wrapping list.
function liftListItem(itemType) {
  return function(state, dispatch) {
    var ref = state.selection;
    var $from = ref.$from;
    var $to = ref.$to;
    var range = $from.blockRange($to, function (node) { return node.childCount && node.firstChild.type == itemType; })
    if (!range) { return false }
    if (!dispatch) { return true }
    if ($from.node(range.depth - 1).type == itemType) // Inside a parent list
      { return liftToOuterList(state, dispatch, itemType, range) }
    else // Outer list node
      { return liftOutOfList(state, dispatch, range) }
  }
}
exports.liftListItem = liftListItem

function liftToOuterList(state, dispatch, itemType, range) {
  var tr = state.tr, end = range.end, endOfList = range.$to.end(range.depth)
  if (end < endOfList) {
    // There are siblings after the lifted items, which must become
    // children of the last item
    tr.step(new ReplaceAroundStep(end - 1, endOfList, end, endOfList,
                                  new Slice(Fragment.from(itemType.create(null, range.parent.copy())), 1, 0), 1, true))
    range = new NodeRange(tr.doc.resolveNoCache(range.$from.pos), tr.doc.resolveNoCache(endOfList), range.depth)
  }
  dispatch(tr.lift(range, liftTarget(range)).scrollIntoView())
  return true
}

function liftOutOfList(state, dispatch, range) {
  var tr = state.tr, list = range.parent
  // Merge the list items into a single big item
  for (var pos = range.end, i = range.endIndex - 1, e = range.startIndex; i > e; i--) {
    pos -= list.child(i).nodeSize
    tr.delete(pos - 1, pos + 1)
  }
  var $start = tr.doc.resolve(range.start), item = $start.nodeAfter
  var atStart = range.startIndex == 0, atEnd = range.endIndex == list.childCount
  var parent = $start.node(-1), indexBefore = $start.index(-1)
  if (!parent.canReplace(indexBefore + (atStart ? 0 : 1), indexBefore + 1,
                         item.content.append(atEnd ? Fragment.empty : Fragment.from(list))))
    { return false }
  var start = $start.pos, end = start + item.nodeSize
  // Strip off the surrounding list. At the sides where we're not at
  // the end of the list, the existing list is closed. At sides where
  // this is the end, it is overwritten to its end.
  tr.step(new ReplaceAroundStep(start - (atStart ? 1 : 0), end + (atEnd ? 1 : 0), start + 1, end - 1,
                                new Slice((atStart ? Fragment.empty : Fragment.from(list.copy(Fragment.empty)))
                                          .append(atEnd ? Fragment.empty : Fragment.from(list.copy(Fragment.empty))),
                                          atStart ? 0 : 1, atEnd ? 0 : 1), atStart ? 0 : 1))
  dispatch(tr.scrollIntoView())
  return true
}

// :: (NodeType) → (state: EditorState, dispatch: ?(tr: Transaction)) → bool
// Create a command to sink the list item around the selection down
// into an inner list.
function sinkListItem(itemType) {
  return function(state, dispatch) {
    var ref = state.selection;
    var $from = ref.$from;
    var $to = ref.$to;
    var range = $from.blockRange($to, function (node) { return node.childCount && node.firstChild.type == itemType; })
    if (!range) { return false }
    var startIndex = range.startIndex
    if (startIndex == 0) { return false }
    var parent = range.parent, nodeBefore = parent.child(startIndex - 1)
    if (nodeBefore.type != itemType) { return false }

    if (dispatch) {
      var nestedBefore = nodeBefore.lastChild && nodeBefore.lastChild.type == parent.type
      var inner = Fragment.from(nestedBefore ? itemType.create() : null)
      var slice = new Slice(Fragment.from(itemType.create(null, Fragment.from(parent.copy(inner)))),
                            nestedBefore ? 3 : 1, 0)
      var before = range.start, after = range.end
      dispatch(state.tr.step(new ReplaceAroundStep(before - (nestedBefore ? 3 : 1), after,
                                                   before, after, slice, 1, true))
               .scrollIntoView())
    }
    return true
  }
}
exports.sinkListItem = sinkListItem


/***/ }),
/* 29 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Mark = ref.Mark;
var ref$1 = __webpack_require__(1);
var NodeSelection = ref$1.NodeSelection;

var ref$2 = __webpack_require__(43);
var scrollRectIntoView = ref$2.scrollRectIntoView;
var posAtCoords = ref$2.posAtCoords;
var coordsAtPos = ref$2.coordsAtPos;
var endOfTextblock = ref$2.endOfTextblock;
var storeScrollPos = ref$2.storeScrollPos;
var resetScrollPos = ref$2.resetScrollPos;
var ref$3 = __webpack_require__(47);
var docViewDesc = ref$3.docViewDesc;
var ref$4 = __webpack_require__(45);
var initInput = ref$4.initInput;
var destroyInput = ref$4.destroyInput;
var dispatchEvent = ref$4.dispatchEvent;
var ensureListeners = ref$4.ensureListeners;
var ref$5 = __webpack_require__(13);
var SelectionReader = ref$5.SelectionReader;
var selectionToDOM = ref$5.selectionToDOM;
var ref$6 = __webpack_require__(22);
var Decoration = ref$6.Decoration;
var viewDecorations = ref$6.viewDecorations;var assign;
((assign = __webpack_require__(22), exports.Decoration = assign.Decoration, exports.DecorationSet = assign.DecorationSet))

// ::- An editor view manages the DOM structure that represents an
// editor. Its state and behavior are determined by its
// [props](#view.EditorProps).
var EditorView = function(place, props) {
  this._props = props
  // :: EditorState
  // The view's current [state](#state.EditorState).
  this.state = props.state

  this.dispatch = this.dispatch.bind(this)

  this._root = null
  this.focused = false

  // :: dom.Element
  // The editable DOM node containing the document. (You probably
  // should not be directly interfering with its child nodes.)
  this.dom = (place && place.mount) || document.createElement("div")
  if (place) {
    if (place.appendChild) { place.appendChild(this.dom) }
    else if (place.apply) { place(this.dom) }
    else if (place.mount) { this.mounted = true }
  }

  this.editable = getEditable(this)
  this.cursorWrapper = null
  updateCursorWrapper(this)
  this.docView = docViewDesc(this.state.doc, computeDocDeco(this), viewDecorations(this), this.dom, this)

  this.lastSelectedViewDesc = null
  this.selectionReader = new SelectionReader(this)
  initInput(this)

  this.pluginViews = []
  this.updatePluginViews()
};

var prototypeAccessors = { props: {},root: {} };

// :: EditorProps
// The view's current [props](#view.EditorProps).
prototypeAccessors.props.get = function () {
    var this$1 = this;

  if (this._props.state != this.state) {
    var prev = this._props
    this._props = {}
    for (var name in prev) { this$1._props[name] = prev[name] }
    this._props.state = this.state
  }
  return this._props
};

// :: (EditorProps)
// Update the view's props. Will immediately cause an update to
// the view's DOM.
EditorView.prototype.update = function (props) {
  if (props.handleDOMEvents != this._props.handleDOMEvents) { ensureListeners(this) }
  this._props = props
  this.updateState(props.state)
};

// :: (EditorProps)
// Update the view by updating existing props object with the object
// given as argument. Equivalent to `view.update(Object.assign({},
// view.props, props))`.
EditorView.prototype.setProps = function (props) {
    var this$1 = this;

  var updated = {}
  for (var name in this$1._props) { updated[name] = this$1._props[name] }
  updated.state = this.state
  for (var name$1 in props) { updated[name$1] = props[name$1] }
  this.update(updated)
};

// :: (EditorState)
// Update the editor's `state` prop, without touching any of the
// other props.
EditorView.prototype.updateState = function (state) {
  var prev = this.state
  this.state = state
  if (prev.plugins != state.plugins) { ensureListeners(this) }

  this.domObserver.flush()
  if (this.inDOMChange && this.inDOMChange.stateUpdated(state)) { return }

  var prevEditable = this.editable
  this.editable = getEditable(this)
  updateCursorWrapper(this)
  var innerDeco = viewDecorations(this), outerDeco = computeDocDeco(this)

  var scrollToSelection = state.scrollToSelection > prev.scrollToSelection || prev.config != state.config
  var updateDoc = !this.docView.matchesNode(state.doc, outerDeco, innerDeco)
  var updateSel = updateDoc || !state.selection.eq(prev.selection) || this.selectionReader.domChanged()
  var oldScrollPos = !scrollToSelection && updateSel && storeScrollPos(this)

  if (updateSel) {
    this.domObserver.stop()
    if (updateDoc) {
      if (!this.docView.update(state.doc, outerDeco, innerDeco, this)) {
        this.docView.destroy()
        this.docView = docViewDesc(state.doc, outerDeco, innerDeco, this.dom, this)
      }
      this.selectionReader.clearDOMState()
    }
    selectionToDOM(this)
    this.domObserver.start()
  }

  if (prevEditable != this.editable) { this.selectionReader.editableChanged() }
  this.updatePluginViews(prev)

  if (scrollToSelection) {
    if (state.selection instanceof NodeSelection)
      { scrollRectIntoView(this, this.docView.domAfterPos(state.selection.from).getBoundingClientRect()) }
    else
      { scrollRectIntoView(this, this.coordsAtPos(state.selection.head)) }
  } else if (oldScrollPos) {
    resetScrollPos(oldScrollPos)
  }
};

EditorView.prototype.destroyPluginViews = function () {
  var view
  while (view = this.pluginViews.pop()) { if (view.destroy) { view.destroy() } }
};

EditorView.prototype.updatePluginViews = function (prevState) {
    var this$1 = this;

  var plugins = this.state.plugins
  if (!prevState || prevState.plugins != plugins) {
    this.destroyPluginViews()
    for (var i = 0; i < plugins.length; i++) {
      var plugin = plugins[i]
      if (plugin.spec.view) { this$1.pluginViews.push(plugin.spec.view(this$1)) }
    }
  } else {
    for (var i$1 = 0; i$1 < this.pluginViews.length; i$1++) {
      var pluginView = this$1.pluginViews[i$1]
      if (pluginView.update) { pluginView.update(this$1, prevState) }
    }
  }
};

// :: () → bool
// Query whether the view has focus.
EditorView.prototype.hasFocus = function () {
  return this.root.activeElement == this.dom
};

// :: (string, (prop: *) → *) → *
// Goes over the values of a prop, first those provided directly,
// then those from plugins (in order), and calls `f` every time a
// non-undefined value is found. When `f` returns a truthy value,
// that is immediately returned. When `f` isn't provided, it is
// treated as the identity function (the prop value is returned
// directly).
EditorView.prototype.someProp = function (propName, f) {
  var prop = this._props && this._props[propName], value
  if (prop != null && (value = f ? f(prop) : prop)) { return value }
  var plugins = this.state.plugins
  if (plugins) { for (var i = 0; i < plugins.length; i++) {
    var prop$1 = plugins[i].props[propName]
    if (prop$1 != null && (value = f ? f(prop$1) : prop$1)) { return value }
  } }
};

// :: ()
// Focus the editor.
EditorView.prototype.focus = function () {
  this.domObserver.stop()
  selectionToDOM(this, true)
  this.domObserver.start()
  if (this.editable) { this.dom.focus() }
};

// :: union<dom.Document, dom.DocumentFragment>
// Get the document root in which the editor exists. This will
// usually be the top-level `document`, but might be a shadow DOM
// root if the editor is inside a shadow DOM.
prototypeAccessors.root.get = function () {
    var this$1 = this;

  var cached = this._root
  if (cached == null) { for (var search = this.dom.parentNode; search; search = search.parentNode) {
    if (search.nodeType == 9 || (search.nodeType == 11 && search.host))
      { return this$1._root = search }
  } }
  return cached || document
};

// :: ({left: number, top: number}) → ?{pos: number, inside: number}
// Given a pair of coordinates, return the document position that
// corresponds to them. May return null if the given coordinates
// aren't inside of the visible editor. When an object is returned,
// its `pos` property is the position nearest to the coordinates,
// and its `inside` property holds the position before the inner
// node that the click happened inside of, or -1 if the click was at
// the top level.
EditorView.prototype.posAtCoords = function (coords) {
  var pos = posAtCoords(this, coords)
  if (this.inDOMChange && pos) {
    pos.pos = this.inDOMChange.mapping.map(pos)
    if (pos.inside != -1) { pos.inside = this.inDOMChange.mapping.map(pos.inside) }
  }
  return pos
};

// :: (number) → {left: number, right: number, top: number, bottom: number}
// Returns the screen rectangle at a given document position. `left`
// and `right` will be the same number, as this returns a flat
// cursor-ish rectangle.
EditorView.prototype.coordsAtPos = function (pos) {
  if (this.inDOMChange)
    { pos = this.inDOMChange.mapping.invert().map(pos) }
  return coordsAtPos(this, pos)
};

// :: (union<"up", "down", "left", "right", "forward", "backward">, ?EditorState) → bool
// Find out whether the selection is at the end of a textblock when
// moving in a given direction. When, for example, given `"left"`,
// it will return true if moving left from the current cursor
// position would leave that position's parent textblock.
EditorView.prototype.endOfTextblock = function (dir, state) {
  return endOfTextblock(this, state || this.state, dir)
};

// :: ()
// Removes the editor from the DOM and destroys all [node
// views](#view.NodeView).
EditorView.prototype.destroy = function () {
  if (!this.docView) { return }
  destroyInput(this)
  this.destroyPluginViews()
  this.selectionReader.destroy()
  if (this.mounted) {
    this.docView.update(this.state.doc, [], viewDecorations(this), this)
    this.dom.textContent = ""
  } else if (this.dom.parentNode) {
    this.dom.parentNode.removeChild(this.dom)
  }
  this.docView.destroy()
  this.docView = null
};

// Used for testing.
EditorView.prototype.dispatchEvent = function (event) {
  return dispatchEvent(this, event)
};

// :: (Transaction)
// Dispatch a transaction. Will call the
// [`dispatchTransaction`](#view.EditorProps.dispatchTransaction) when given,
// and defaults to applying the transaction to the current state and
// calling [`updateState`](#view.EditorView.updateState) otherwise.
// This method is bound to the view instance, so that it can be
// easily passed around.
EditorView.prototype.dispatch = function (tr) {
  var dispatchTransaction = this._props.dispatchTransaction
  if (dispatchTransaction) { dispatchTransaction(tr) }
  else { this.updateState(this.state.apply(tr)) }
};

Object.defineProperties( EditorView.prototype, prototypeAccessors );
exports.EditorView = EditorView

function computeDocDeco(view) {
  var attrs = Object.create(null)
  attrs.class = "ProseMirror" + (view.focused ? " ProseMirror-focused" : "")
  attrs.contenteditable = String(view.editable)

  view.someProp("attributes", function (value) {
    if (typeof value == "function") { value = value(view.state) }
    if (value) { for (var attr in value) {
      if (attr == "class")
        { attrs.class += " " + value[attr] }
      else if (!attrs[attr] && attr != "contenteditable" && attr != "nodeName")
        { attrs[attr] = String(value[attr]) }
    } }
  })

  return [Decoration.node(0, view.state.doc.content.size, attrs)]
}

function nonInclusiveMark(mark) {
  return mark.type.spec.inclusive === false
}

function cursorWrapperDOM() {
  var span = document.createElement("span")
  span.textContent = "\ufeff" // zero-width non-breaking space
  return span
}

function updateCursorWrapper(view) {
  var ref = view.state.selection;
  var $cursor = ref.$cursor;
  if ($cursor && (view.state.storedMarks ||
                  $cursor.parent.content.length == 0 ||
                  $cursor.parentOffset && !$cursor.textOffset && $cursor.nodeBefore.marks.some(nonInclusiveMark))) {
    // Needs a cursor wrapper
    var marks = view.state.storedMarks || $cursor.marks()
    var spec = {isCursorWrapper: true, marks: marks, raw: true}
    if (!view.cursorWrapper || !Mark.sameSet(view.cursorWrapper.spec.marks, marks) ||
        view.cursorWrapper.type.widget.textContent != "\ufeff")
      { view.cursorWrapper = Decoration.widget($cursor.pos, cursorWrapperDOM(), spec) }
    else if (view.cursorWrapper.pos != $cursor.pos)
      { view.cursorWrapper = Decoration.widget($cursor.pos, view.cursorWrapper.type.widget, spec) }
  } else {
    view.cursorWrapper = null
  }
}

function getEditable(view) {
  return !view.someProp("editable", function (value) { return value(view.state) === false; })
}

// EditorProps:: interface
//
// The configuration object that can be passed to an editor view. It
// supports the following properties (only `state` is required).
//
// The various event-handling functions may all return `true` to
// indicate that they handled the given event. The view will then take
// care to call `preventDefault` on the event, except with
// `handleDOMEvents`, where the handler itself is responsible for that.
//
// Except for `state` and `dispatchTransaction`, these may also be
// present on the `props` property of plugins. How a prop is resolved
// depends on the prop. Handler functions are called one at a time,
// starting with the plugins (in order of appearance), and finally
// looking at the base props, until one of them returns true. For some
// props, the first plugin that yields a value gets precedence. For
// `class`, all the classes returned are combined.
//
//   state:: EditorState
//   The state of the editor.
//
//   dispatchTransaction:: ?(tr: Transaction)
//   The callback over which to send transactions (state updates)
//   produced by the view. You'll usually want to make sure this ends
//   up calling the view's
//   [`updateState`](#view.EditorView.updateState) method with a new
//   state that has the transaction
//   [applied](#state.EditorState.apply).
//
//   handleDOMEvents:: ?Object<(view: EditorView, event: dom.Event) → bool>
//   Can be an object mapping DOM event type names to functions that
//   handle them. Such functions will be called before any handling
//   ProseMirror does of events fired on the editable DOM element.
//   Contrary to the other event handling props, when returning true
//   from such a function, you are responsible for calling
//   `preventDefault` yourself (or not, if you want to allow the
//   default behavior).
//
//   handleKeyDown:: ?(view: EditorView, event: dom.KeyboardEvent) → bool
//   Called when the editor receives a `keydown` event.
//
//   handleKeyPress:: ?(view: EditorView, event: dom.KeyboardEvent) → bool
//   Handler for `keypress` events.
//
//   handleTextInput:: ?(view: EditorView, from: number, to: number, text: string) → bool
//   Whenever the user directly input text, this handler is called
//   before the input is applied. If it returns `true`, the default
//   effect of actually inserting the text is suppressed.
//
//   handleClickOn:: ?(view: EditorView, pos: number, node: Node, nodePos: number, event: dom.MouseEvent, direct: bool) → bool
//   Called for each node around a click, from the inside out. The
//   `direct` flag will be true for the inner node.
//
//   handleClick:: ?(view: EditorView, pos: number, event: dom.MouseEvent) → bool
//   Called when the editor is clicked, after `handleClickOn` handlers
//   have been called.
//
//   handleDoubleClickOn:: ?(view: EditorView, pos: number, node: Node, nodePos: number, event: dom.MouseEvent, direct: bool) → bool
//   Called for each node around a double click.
//
//   handleDoubleClick:: ?(view: EditorView, pos: number, event: dom.MouseEvent) → bool
//   Called when the editor is double-clicked, after `handleDoubleClickOn`.
//
//   handleTripleClickOn:: ?(view: EditorView, pos: number, node: Node, nodePos: number, event: dom.MouseEvent, direct: bool) → bool
//   Called for each node around a triple click.
//
//   handleTripleClick:: ?(view: EditorView, pos: number, event: dom.MouseEvent) → bool
//   Called when the editor is triple-clicked, after `handleTripleClickOn`.
//
//   handleContextMenu:: ?(view: EditorView, pos: number, event: dom.MouseEvent) → bool
//   Called when a context menu event is fired in the editor.
//
//   handlePaste:: ?(view: EditorView, event: dom.Event, slice: Slice) → bool
//   Can be used to override the behavior of pasting. `slice` is the
//   pasted content parsed by the editor, but you can directly access
//   the event to get at the raw content.
//
//   handleDrop:: ?(view: EditorView, event: dom.Event, slice: Slice, moved: bool) → bool
//   Called when something is dropped on the editor. `moved` will be
//   true if this drop moves from the current selection (which should
//   thus be deleted).
//
//   onFocus:: ?(view: EditorView, event: dom.Event)
//   Called when the editor is focused.
//
//   onBlur:: ?(view: EditorView, event: dom.Event)
//   Called when the editor loses focus.
//
//   createSelectionBetween:: ?(view: EditorView, anchor: ResolvedPos, head: ResolvedPos) → ?Selection
//   Can be used to override the selection object created when reading
//   a DOM selection between the given anchor and head.
//
//   domParser:: ?DOMParser
//   The [parser](#model.DOMParser) to use when reading editor changes
//   from the DOM. Defaults to calling
//   [`DOMParser.fromSchema`](#model.DOMParser^fromSchema) on the
//   editor's schema.
//
//   clipboardParser:: ?DOMParser
//   The [parser](#model.DOMParser) to use when reading content from
//   the clipboard. When not given, the value of the
//   [`domParser`](#view.EditorProps.domParser) prop is used.
//
//   transformPasted:: ?(Slice) → Slice
//   Can be used to transform pasted content before it is applied to
//   the document.
//
//   transformPastedHTML:: ?(string) → string
//   Can be used to transform pasted HTML text, _before_ it is parsed,
//   for example to clean it up.
//
//   transformPastedText:: ?(string) → string
//   Transform pasted plain text.
//
//   nodeViews:: ?Object<(node: Node, view: EditorView, getPos: () → number, decorations: [Decoration]) → NodeView>
//   Allows you to pass custom rendering and behavior logic for nodes
//   and marks. Should map node and mark names to constructor function
//   that produce a [`NodeView`](#view.NodeView) object implementing
//   the node's display behavior. `getPos` is a function that can be
//   called to get the node's current position, which can be useful
//   when creating transactions that update it.
//
//   `decorations` is an array of node or inline decorations that are
//   active around the node. They are automatically drawn in the
//   normal way, and you will usually just want to ignore this, but
//   they can also be used as a way to provide context information to
//   the node view without adding it to the document itself.
//
//   clipboardSerializer:: ?DOMSerializer
//   The DOM serializer to use when putting content onto the
//   clipboard. If not given, the result of
//   [`DOMSerializer.fromSchema`](#model.DOMSerializer^fromSchema)
//   will be used.
//
//   decorations:: ?(EditorState) → ?DecorationSet
//   A set of [document decorations](#view.Decoration) to add to the
//   view.
//
//   editable:: ?(EditorState) → bool
//   When this returns false, the content of the view is not directly
//   editable.
//
//   attributes:: ?union<Object<string>, (EditorState) → ?Object<string>>
//   Control the DOM attributes of the editable element. May be either
//   an object or a function going from an editor state to an object.
//   By default, the element will get a class `"ProseMirror"`, and
//   will have its `contentEditable` attribute determined by the
//   [`editable` prop](#view.EditorProps.editable). Additional classes
//   provided here will be added to the class. For other attributes,
//   the value provided first (as in
//   [`someProp`](#view.EditorView.someProp)) will be used.
//
//   scrollThreshold:: ?number
//   Determines the distance (in pixels) between the cursor and the
//   end of the visible viewport at which point, when scrolling the
//   cursor into view, scrolling takes place. Defaults to 0.
//
//   scrollMargin:: ?number
//   Determines the extra space (in pixels) that is left above or
//   below the cursor when it is scrolled into view. Defaults to 5.


/***/ }),
/* 30 */
/***/ (function(module, exports) {

// ::- Persistent data structure representing an ordered mapping from
// strings to values, with some convenient update methods.
function OrderedMap(content) {
  this.content = content
}

OrderedMap.prototype = {
  constructor: OrderedMap,

  find: function(key) {
    for (var i = 0; i < this.content.length; i += 2)
      if (this.content[i] === key) return i
    return -1
  },

  // :: (string) → ?any
  // Retrieve the value stored under `key`, or return undefined when
  // no such key exists.
  get: function(key) {
    var found = this.find(key)
    return found == -1 ? undefined : this.content[found + 1]
  },

  // :: (string, any, ?string) → OrderedMap
  // Create a new map by replacing the value of `key` with a new
  // value, or adding a binding to the end of the map. If `newKey` is
  // given, the key of the binding will be replaced with that key.
  update: function(key, value, newKey) {
    var self = newKey && newKey != key ? this.remove(newKey) : this
    var found = self.find(key), content = self.content.slice()
    if (found == -1) {
      content.push(newKey || key, value)
    } else {
      content[found + 1] = value
      if (newKey) content[found] = newKey
    }
    return new OrderedMap(content)
  },

  // :: (string) → OrderedMap
  // Return a map with the given key removed, if it existed.
  remove: function(key) {
    var found = this.find(key)
    if (found == -1) return this
    var content = this.content.slice()
    content.splice(found, 2)
    return new OrderedMap(content)
  },

  // :: (string, any) → OrderedMap
  // Add a new key to the start of the map.
  addToStart: function(key, value) {
    return new OrderedMap([key, value].concat(this.remove(key).content))
  },

  // :: (string, any) → OrderedMap
  // Add a new key to the end of the map.
  addToEnd: function(key, value) {
    var content = this.remove(key).content.slice()
    content.push(key, value)
    return new OrderedMap(content)
  },

  // :: (string, string, any) → OrderedMap
  // Add a key after the given key. If `place` is not found, the new
  // key is added to the end.
  addBefore: function(place, key, value) {
    var without = this.remove(key), content = without.content.slice()
    var found = without.find(place)
    content.splice(found == -1 ? content.length : found, 0, key, value)
    return new OrderedMap(content)
  },

  // :: ((key: string, value: any))
  // Call the given function for each key/value pair in the map, in
  // order.
  forEach: function(f) {
    for (var i = 0; i < this.content.length; i += 2)
      f(this.content[i], this.content[i + 1])
  },

  // :: (union<Object, OrderedMap>) → OrderedMap
  // Create a new map by prepending the keys in this map that don't
  // appear in `map` before the keys in `map`.
  prepend: function(map) {
    map = OrderedMap.from(map)
    if (!map.size) return this
    return new OrderedMap(map.content.concat(this.subtract(map).content))
  },

  // :: (union<Object, OrderedMap>) → OrderedMap
  // Create a new map by appending the keys in this map that don't
  // appear in `map` after the keys in `map`.
  append: function(map) {
    map = OrderedMap.from(map)
    if (!map.size) return this
    return new OrderedMap(this.subtract(map).content.concat(map.content))
  },

  // :: (union<Object, OrderedMap>) → OrderedMap
  // Create a map containing all the keys in this map that don't
  // appear in `map`.
  subtract: function(map) {
    var result = this
    map = OrderedMap.from(map)
    for (var i = 0; i < map.content.length; i += 2)
      result = result.remove(map.content[i])
    return result
  },

  // :: number
  // The amount of keys in this map.
  get size() {
    return this.content.length >> 1
  }
}

// :: (?union<Object, OrderedMap>) → OrderedMap
// Return a map with the given content. If null, create an empty
// map. If given an ordered map, return that map itself. If given an
// object, create a map from the object's properties.
OrderedMap.from = function(value) {
  if (value instanceof OrderedMap) return value
  var content = []
  if (value) for (var prop in value) content.push(prop, value[prop])
  return new OrderedMap(content)
}

module.exports = OrderedMap


/***/ }),
/* 31 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(10);
var InputRule = ref.InputRule;

// :: InputRule Converts double dashes to an emdash.
var emDash = new InputRule(/--$/, "—")
exports.emDash = emDash
// :: InputRule Converts three dots to an ellipsis character.
var ellipsis = new InputRule(/\.\.\.$/, "…")
exports.ellipsis = ellipsis
// :: InputRule “Smart” opening double quotes.
var openDoubleQuote = new InputRule(/(?:^|[\s\{\[\(\<'"\u2018\u201C])(")$/, "“")
exports.openDoubleQuote = openDoubleQuote
// :: InputRule “Smart” closing double quotes.
var closeDoubleQuote = new InputRule(/"$/, "”")
exports.closeDoubleQuote = closeDoubleQuote
// :: InputRule “Smart” opening single quotes.
var openSingleQuote = new InputRule(/(?:^|[\s\{\[\(\<'"\u2018\u201C])(')$/, "‘")
exports.openSingleQuote = openSingleQuote
// :: InputRule “Smart” closing single quotes.
var closeSingleQuote = new InputRule(/'$/, "’")
exports.closeSingleQuote = closeSingleQuote

// :: [InputRule] Smart-quote related input rules.
var smartQuotes = [openDoubleQuote, closeDoubleQuote, openSingleQuote, closeSingleQuote]
exports.smartQuotes = smartQuotes

// :: [InputRule] All schema-independent input rules defined in this module.
var allInputRules = [emDash, ellipsis].concat(smartQuotes)
exports.allInputRules = allInputRules


/***/ }),
/* 32 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(10);
var InputRule = ref.InputRule;
var ref$1 = __webpack_require__(2);
var findWrapping = ref$1.findWrapping;
var canJoin = ref$1.canJoin;

// :: (RegExp, NodeType, ?union<Object, ([string]) → ?Object>, ?([string], Node) → bool) → InputRule
// Build an input rule for automatically wrapping a textblock when a
// given string is typed. The `regexp` argument is
// directly passed through to the `InputRule` constructor. You'll
// probably want the regexp to start with `^`, so that the pattern can
// only occur at the start of a textblock.
//
// `nodeType` is the type of node to wrap in. If it needs attributes,
// you can either pass them directly, or pass a function that will
// compute them from the regular expression match.
//
// By default, if there's a node with the same type above the newly
// wrapped node, the rule will try to [join](#transform.Transform.join) those
// two nodes. You can pass a join predicate, which takes a regular
// expression match and the node before the wrapped node, and can
// return a boolean to indicate whether a join should happen.
function wrappingInputRule(regexp, nodeType, getAttrs, joinPredicate) {
  return new InputRule(regexp, function (state, match, start, end) {
    var attrs = getAttrs instanceof Function ? getAttrs(match) : getAttrs
    var tr = state.tr.delete(start, end)
    var $start = tr.doc.resolve(start), range = $start.blockRange(), wrapping = range && findWrapping(range, nodeType, attrs)
    if (!wrapping) { return null }
    tr.wrap(range, wrapping)
    var before = tr.doc.resolve(start - 1).nodeBefore
    if (before && before.type == nodeType && canJoin(tr.doc, start - 1) &&
        (!joinPredicate || joinPredicate(match, before)))
      { tr.join(start - 1) }
    return tr
  })
}
exports.wrappingInputRule = wrappingInputRule

// :: (RegExp, NodeType, ?union<Object, ([string]) → ?Object>) → InputRule
// Build an input rule that changes the type of a textblock when the
// matched text is typed into it. You'll usually want to start your
// regexp with `^` to that it is only matched at the start of a
// textblock. The optional `getAttrs` parameter can be used to compute
// the new node's attributes, and works the same as in the
// `wrappingInputRule` function.
function textblockTypeInputRule(regexp, nodeType, getAttrs) {
  return new InputRule(regexp, function (state, match, start, end) {
    var $start = state.doc.resolve(start)
    var attrs = getAttrs instanceof Function ? getAttrs(match) : getAttrs
    if (!$start.node(-1).canReplaceWith($start.index(-1), $start.indexAfter(-1), nodeType, attrs)) { return null }
    return state.tr
      .delete(start, end)
      .setBlockType(start, start, nodeType, attrs)
  })
}
exports.textblockTypeInputRule = textblockTypeInputRule


// :: (NodeType) → InputRule
// Given a blockquote node type, returns an input rule that turns `"> "`
// at the start of a textblock into a blockquote.
function blockQuoteRule(nodeType) {
  return wrappingInputRule(/^\s*> $/, nodeType)
}
exports.blockQuoteRule = blockQuoteRule

// :: (NodeType) → InputRule
// Given a list node type, returns an input rule that turns a number
// followed by a dot at the start of a textblock into an ordered list.
function orderedListRule(nodeType) {
  return wrappingInputRule(/^(\d+)\. $/, nodeType, function (match) { return ({order: +match[1]}); },
                           function (match, node) { return node.childCount + node.attrs.order == +match[1]; })
}
exports.orderedListRule = orderedListRule

// :: (NodeType) → InputRule
// Given a list node type, returns an input rule that turns a bullet
// (dash, plush, or asterisk) at the start of a textblock into a
// bullet list.
function bulletListRule(nodeType) {
  return wrappingInputRule(/^\s*([-+*]) $/, nodeType)
}
exports.bulletListRule = bulletListRule

// :: (NodeType) → InputRule
// Given a code block node type, returns an input rule that turns a
// textblock starting with three backticks into a code block.
function codeBlockRule(nodeType) {
  return textblockTypeInputRule(/^```$/, nodeType)
}
exports.codeBlockRule = codeBlockRule

// :: (NodeType, number) → InputRule
// Given a node type and a maximum level, creates an input rule that
// turns up to that number of `#` characters followed by a space at
// the start of a textblock into a heading whose level corresponds to
// the number of `#` signs.
function headingRule(nodeType, maxLevel) {
  return textblockTypeInputRule(new RegExp("^(#{1," + maxLevel + "}) $"),
                                nodeType, function (match) { return ({level: match[1].length}); })
}
exports.headingRule = headingRule


/***/ }),
/* 33 */
/***/ (function(module, exports) {

function findDiffStart(a, b, pos) {
  for (var i = 0;; i++) {
    if (i == a.childCount || i == b.childCount)
      { return a.childCount == b.childCount ? null : pos }

    var childA = a.child(i), childB = b.child(i)
    if (childA == childB) { pos += childA.nodeSize; continue }

    if (!childA.sameMarkup(childB)) { return pos }

    if (childA.isText && childA.text != childB.text) {
      for (var j = 0; childA.text[j] == childB.text[j]; j++)
        { pos++ }
      return pos
    }
    if (childA.content.size || childB.content.size) {
      var inner = findDiffStart(childA.content, childB.content, pos + 1)
      if (inner != null) { return inner }
    }
    pos += childA.nodeSize
  }
}
exports.findDiffStart = findDiffStart

function findDiffEnd(a, b, posA, posB) {
  for (var iA = a.childCount, iB = b.childCount;;) {
    if (iA == 0 || iB == 0)
      { return iA == iB ? null : {a: posA, b: posB} }

    var childA = a.child(--iA), childB = b.child(--iB), size = childA.nodeSize
    if (childA == childB) {
      posA -= size; posB -= size
      continue
    }

    if (!childA.sameMarkup(childB)) { return {a: posA, b: posB} }

    if (childA.isText && childA.text != childB.text) {
      var same = 0, minSize = Math.min(childA.text.length, childB.text.length)
      while (same < minSize && childA.text[childA.text.length - same - 1] == childB.text[childB.text.length - same - 1]) {
        same++; posA--; posB--
      }
      return {a: posA, b: posB}
    }
    if (childA.content.size || childB.content.size) {
      var inner = findDiffEnd(childA.content, childB.content, posA - 1, posB - 1)
      if (inner) { return inner }
    }
    posA -= size; posB -= size
  }
}
exports.findDiffEnd = findDiffEnd


/***/ }),
/* 34 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(3);
var Fragment = ref.Fragment;
var ref$1 = __webpack_require__(11);
var Slice = ref$1.Slice;
var ref$2 = __webpack_require__(4);
var Mark = ref$2.Mark;

// ParseRule:: interface
// A value that describes how to parse a given DOM node or inline
// style as a ProseMirror node or mark.
//
//   tag:: ?string
//   A CSS selector describing the kind of DOM elements to match. A
//   single rule should have _either_ a `tag` or a `style` property.
//
//   style:: ?string
//   A CSS property name to match. When given, this rule matches
//   inline styles that list that property.
//
//   context:: ?string
//   When given, restricts this rule to only match when the current
//   context—the parent nodes into which the content is being
//   parsed—matches this expression. Should contain one or more node
//   names or node group names followed by single or double slashes.
//   For example `"paragraph/"` means the rule only matches when the
//   parent node is a paragraph, `"blockquote/paragraph/"` restricts
//   it to be in a paragraph that is inside a blockquote, and
//   `"section//"` matches any position inside a section—a double
//   slash matches any sequence of ancestor nodes.
//
//   node:: ?string
//   The name of the node type to create when this rule matches. Only
//   valid for rules with a `tag` property, not for style rules. Each
//   rule should have one of a `node`, `mark`, or `ignore` property
//   (except when it appears in a [node](#model.NodeSpec.parseDOM) or
//   [mark spec](#model.MarkSpec.parseDOM), in which case the `node`
//   or `mark` property will be derived from its position).
//
//   mark:: ?string
//   The name of the mark type to wrap the matched content in.
//
//   priority:: ?number
//   Can be used to change the order in which the parse rules in a
//   schema are tried. Those with higher priority come first. Rules
//   without a priority are counted as having priority 50. This
//   property is only meaningful in a schema—when directly
//   constructing a parser, the order of the rule array is used.
//
//   ignore:: ?bool
//   When true, ignore content that matches this rule.
//
//   skip:: ?bool
//   When true, ignore the node that matches this rule, but do parse
//   its content.
//
//   attrs:: ?Object
//   Attributes for the node or mark created by this rule. When
//   `getAttrs` is provided, it takes precedence.
//
//   getAttrs:: ?(union<dom.Node, string>) → ?union<bool, Object>
//   A function used to compute the attributes for the node or mark
//   created by this rule. Can also be used to describe further
//   conditions the DOM element or style must match. When it returns
//   `false`, the rule won't match. When it returns null or undefined,
//   that is interpreted as an empty/default set of attributes.
//
//   Called with a DOM Element for `tag` rules, and with a string (the
//   style's value) for `style` rules.
//
//   contentElement:: ?string
//   For `tag` rules that produce non-leaf nodes or marks, by default
//   the content of the DOM element is parsed as content of the mark
//   or node. If the child nodes are in a descendent node, this may be
//   a CSS selector string that the parser must use to find the actual
//   content element.
//
//   getContent:: ?(dom.Node) → Fragment
//   Can be used to override the content of a matched node. Will be
//   called, and its result used, instead of parsing the node's child
//   nodes.
//
//   preserveWhitespace:: ?union<bool, "full">
//   Controls whether whitespace should be preserved when parsing the
//   content inside the matched element. `false` means whitespace may
//   be collapsed, `true` means that whitespace should be preserved
//   but newlines normalized to spaces, and `"full"` means that
//   newlines should also be preserved.

// ::- A DOM parser represents a strategy for parsing DOM content into
// a ProseMirror document conforming to a given schema. Its behavior
// is defined by an array of [rules](#model.ParseRule).
var DOMParser = function(schema, rules) {
  var this$1 = this;

  // :: Schema
  this.schema = schema
  // :: [ParseRule]
  this.rules = rules
  this.tags = []
  this.styles = []

  rules.forEach(function (rule) {
    if (rule.tag) { this$1.tags.push(rule) }
    else if (rule.style) { this$1.styles.push(rule) }
  })
};

// :: (dom.Node, ?Object) → Node
// Parse a document from the content of a DOM node.
//
// options::- Configuration options.
//
//   preserveWhitespace:: ?union<bool, "full">
//   By default, whitespace is collapsed as per HTML's rules. Pass
//   `true` to preserve whitespace, but normalize newlines to
//   spaces, and `"full"` to preserve whitespace entirely.
//
//   findPositions:: ?[{node: dom.Node, offset: number}]
//   When given, the parser will, beside parsing the content,
//   record the document positions of the given DOM positions. It
//   will do so by writing to the objects, adding a `pos` property
//   that holds the document position. DOM positions that are not
//   in the parsed content will not be written to.
//
//   from:: ?number
//   The child node index to start parsing from.
//
//   to:: ?number
//   The child node index to stop parsing at.
//
//   topNode:: ?Node
//   By default, the content is parsed into the schema's default
//   [top node type](#model.Schema.topNodeType). You can pass this
//   option to use the type and attributes from a different node
//   as the top container.
//
//   topStart:: ?number
//   Can be used to influence the content match at the start of
//   the topnode. When given, should be a valid index into
//   `topNode`.
//
//   context:: ?ResolvedPos
//   A set of additional node names to count as
//   [context](#model.ParseRule.context) when parsing, above the
//   given [top node](#model.DOMParser.parse^options.topNode).
DOMParser.prototype.parse = function (dom, options) {
    if ( options === void 0 ) options = {};

  var context = new ParseContext(this, options, false)
  context.addAll(dom, null, options.from, options.to)
  return context.finish()
};

// :: (dom.Node, ?Object) → Slice
// Parses the content of the given DOM node, like
// [`parse`](#model.DOMParser.parse), and takes the same set of
// options. But unlike that method, which produces a whole node,
// this one returns a slice that is open at the sides, meaning that
// the schema constraints aren't applied to the start of nodes to
// the left of the input and the end of nodes at the end.
DOMParser.prototype.parseSlice = function (dom, options) {
    if ( options === void 0 ) options = {};

  var context = new ParseContext(this, options, true)
  context.addAll(dom, null, options.from, options.to)
  return Slice.maxOpen(context.finish())
};

DOMParser.prototype.matchTag = function (dom, context) {
    var this$1 = this;

  for (var i = 0; i < this.tags.length; i++) {
    var rule = this$1.tags[i]
    if (matches(dom, rule.tag) && (!rule.context || context.matchesContext(rule.context))) {
      if (rule.getAttrs) {
        var result = rule.getAttrs(dom)
        if (result === false) { continue }
        rule.attrs = result
      }
      return rule
    }
  }
};

DOMParser.prototype.matchStyle = function (prop, value, context) {
    var this$1 = this;

  for (var i = 0; i < this.styles.length; i++) {
    var rule = this$1.styles[i]
    if (rule.style == prop && (!rule.context || context.matchesContext(rule.context))) {
      if (rule.getAttrs) {
        var result = rule.getAttrs(value)
        if (result === false) { continue }
        rule.attrs = result
      }
      return rule
    }
  }
};

// :: (Schema) → [ParseRule]
// Extract the parse rules listed in a schema's [node
// specs](#model.NodeSpec.parseDOM).
DOMParser.schemaRules = function (schema) {
  var result = []
  function insert(rule) {
    var priority = rule.priority == null ? 50 : rule.priority, i = 0
    for (; i < result.length; i++) {
      var next = result[i], nextPriority = next.priority == null ? 50 : next.priority
      if (nextPriority < priority) { break }
    }
    result.splice(i, 0, rule)
  }

  var loop = function ( name ) {
    var rules = schema.marks[name].spec.parseDOM
    if (rules) { rules.forEach(function (rule) {
      insert(rule = copy(rule))
      rule.mark = name
    }) }
  };

    for (var name in schema.marks) loop( name );
  var loop$1 = function ( name ) {
    var rules$1 = schema.nodes[name$1].spec.parseDOM
    if (rules$1) { rules$1.forEach(function (rule) {
      insert(rule = copy(rule))
      rule.node = name$1
    }) }
  };

    for (var name$1 in schema.nodes) loop$1( name );
  return result
};

// :: (Schema) → DOMParser
// Construct a DOM parser using the parsing rules listed in a
// schema's [node specs](#model.NodeSpec.parseDOM).
DOMParser.fromSchema = function (schema) {
  return schema.cached.domParser ||
    (schema.cached.domParser = new DOMParser(schema, DOMParser.schemaRules(schema)))
};
exports.DOMParser = DOMParser

// : Object<bool> The block-level tags in HTML5
var blockTags = {
  address: true, article: true, aside: true, blockquote: true, canvas: true,
  dd: true, div: true, dl: true, fieldset: true, figcaption: true, figure: true,
  footer: true, form: true, h1: true, h2: true, h3: true, h4: true, h5: true,
  h6: true, header: true, hgroup: true, hr: true, li: true, noscript: true, ol: true,
  output: true, p: true, pre: true, section: true, table: true, tfoot: true, ul: true
}

// : Object<bool> The tags that we normally ignore.
var ignoreTags = {
  head: true, noscript: true, object: true, script: true, style: true, title: true
}

// : Object<bool> List tags.
var listTags = {ol: true, ul: true}

// Using a bitfield for node context options
var OPT_PRESERVE_WS = 1, OPT_PRESERVE_WS_FULL = 2, OPT_OPEN_LEFT = 4

function wsOptionsFor(preserveWhitespace) {
  return (preserveWhitespace ? OPT_PRESERVE_WS : 0) | (preserveWhitespace === "full" ? OPT_PRESERVE_WS_FULL : 0)
}

var NodeContext = function(type, attrs, solid, match, options) {
  this.type = type
  this.attrs = attrs
  this.solid = solid
  this.match = match || (options & OPT_OPEN_LEFT ? null : type.contentExpr.start(attrs))
  this.options = options
  this.content = []
};

NodeContext.prototype.findWrapping = function (type, attrs) {
  if (!this.match) {
    if (!this.type) { return [] }
    var found = this.type.contentExpr.atType(this.attrs, type, attrs)
    if (!found) {
      var start = this.type.contentExpr.start(this.attrs), wrap
      if (wrap = start.findWrapping(type, attrs)) {
        this.match = start
        return wrap
      }
    }
    if (found) { this.match = found }
    else { return null }
  }
  return this.match.findWrapping(type, attrs)
};

NodeContext.prototype.finish = function (openRight) {
  if (!(this.options & OPT_PRESERVE_WS)) { // Strip trailing whitespace
    var last = this.content[this.content.length - 1], m
    if (last && last.isText && (m = /\s+$/.exec(last.text))) {
      if (last.text.length == m[0].length) { this.content.pop() }
      else { this.content[this.content.length - 1] = last.withText(last.text.slice(0, last.text.length - m[0].length)) }
    }
  }
  var content = Fragment.from(this.content)
  if (!openRight && this.match)
    { content = content.append(this.match.fillBefore(Fragment.empty, true)) }
  return this.type ? this.type.create(this.attrs, content) : content
};

var ParseContext = function(parser, options, open) {
  // : DOMParser The parser we are using.
  this.parser = parser
  // : Object The options passed to this parse.
  this.options = options
  this.isOpen = open
  var topNode = options.topNode, topContext
  var topOptions = wsOptionsFor(options.preserveWhitespace) | (open ? OPT_OPEN_LEFT : 0)
  if (topNode)
    { topContext = new NodeContext(topNode.type, topNode.attrs, true,
                                 topNode.contentMatchAt(options.topStart || 0), topOptions) }
  else if (open)
    { topContext = new NodeContext(null, null, true, null, topOptions) }
  else
    { topContext = new NodeContext(parser.schema.topNodeType, null, true, null, topOptions) }
  this.nodes = [topContext]
  // : [Mark] The current set of marks
  this.marks = Mark.none
  this.open = 0
  this.find = options.findPositions
};

var prototypeAccessors = { top: {},currentPos: {} };

prototypeAccessors.top.get = function () {
  return this.nodes[this.open]
};

// : (Mark) → [Mark]
// Add a mark to the current set of marks, return the old set.
ParseContext.prototype.addMark = function (mark) {
  var old = this.marks
  this.marks = mark.addToSet(this.marks)
  return old
};

// : (dom.Node)
// Add a DOM node to the content. Text is inserted as text node,
// otherwise, the node is passed to `addElement` or, if it has a
// `style` attribute, `addElementWithStyles`.
ParseContext.prototype.addDOM = function (dom) {
  if (dom.nodeType == 3) {
    this.addTextNode(dom)
  } else if (dom.nodeType == 1) {
    var style = dom.getAttribute("style")
    if (style) { this.addElementWithStyles(parseStyles(style), dom) }
    else { this.addElement(dom) }
  }
};

ParseContext.prototype.addTextNode = function (dom) {
  var value = dom.nodeValue
  var top = this.top
  if ((top.type && top.type.inlineContent) || /\S/.test(value)) {
    if (!(top.options & OPT_PRESERVE_WS)) {
      value = value.replace(/\s+/g, " ")
      // If this starts with whitespace, and there is either no node
      // before it or a node that ends with whitespace, strip the
      // leading space.
      if (/^\s/.test(value) && this.open == this.nodes.length - 1) {
        var nodeBefore = top.content[top.content.length - 1]
        if (!nodeBefore || nodeBefore.isText && /\s$/.test(nodeBefore.text))
          { value = value.slice(1) }
      }
    } else if (!(top.options & OPT_PRESERVE_WS_FULL)) {
      value = value.replace(/\r?\n|\r/g, " ")
    }
    if (value) { this.insertNode(this.parser.schema.text(value, this.marks)) }
    this.findInText(dom)
  } else {
    this.findInside(dom)
  }
};

// : (dom.Element)
// Try to find a handler for the given tag and use that to parse. If
// none is found, the element's content nodes are added directly.
ParseContext.prototype.addElement = function (dom) {
  var name = dom.nodeName.toLowerCase()
  if (listTags.hasOwnProperty(name)) { normalizeList(dom) }
  var rule = (this.options.ruleFromNode && this.options.ruleFromNode(dom)) || this.parser.matchTag(dom, this)
  if (rule ? rule.ignore : ignoreTags.hasOwnProperty(name)) {
    this.findInside(dom)
  } else if (!rule || rule.skip) {
    if (rule && rule.skip.nodeType) { dom = rule.skip }
    var sync = blockTags.hasOwnProperty(name) && this.top
    this.addAll(dom)
    if (sync) { this.sync(sync) }
  } else {
    this.addElementByRule(dom, rule)
  }
};

// Run any style parser associated with the node's styles. After
// that, if no style parser suppressed the node's content, pass it
// through to `addElement`.
ParseContext.prototype.addElementWithStyles = function (styles, dom) {
    var this$1 = this;

  var oldMarks = this.marks, ignore = false
  for (var i = 0; i < styles.length; i += 2) {
    var rule = this$1.parser.matchStyle(styles[i], styles[i + 1], this$1)
    if (!rule) { continue }
    if (rule.ignore) { ignore = true; break }
    this$1.addMark(this$1.parser.schema.marks[rule.mark].create(rule.attrs))
  }
  if (!ignore) { this.addElement(dom) }
  this.marks = oldMarks
};

// : (dom.Element, ParseRule) → bool
// Look up a handler for the given node. If none are found, return
// false. Otherwise, apply it, use its return value to drive the way
// the node's content is wrapped, and return true.
ParseContext.prototype.addElementByRule = function (dom, rule) {
    var this$1 = this;

  var sync, before, nodeType, markType, mark
  if (rule.node) {
    nodeType = this.parser.schema.nodes[rule.node]
    if (nodeType.isLeaf) { this.insertNode(nodeType.create(rule.attrs, null, this.marks)) }
    else { sync = this.enter(nodeType, rule.attrs, rule.preserveWhitespace) && this.top }
  } else {
    markType = this.parser.schema.marks[rule.mark]
    before = this.addMark(mark = markType.create(rule.attrs))
  }

  if (nodeType && nodeType.isLeaf) {
    this.findInside(dom)
  } else if (rule.getContent) {
    this.findInside(dom)
    rule.getContent(dom).forEach(function (node) { return this$1.insertNode(mark ? node.mark(mark.addToSet(node.marks)) : node); })
  } else {
    var contentDOM = rule.contentElement
    if (typeof contentDOM == "string") { contentDOM = dom.querySelector(contentDOM) }
    if (!contentDOM) { contentDOM = dom }
    this.findAround(dom, contentDOM, true)
    this.addAll(contentDOM, sync)
  }
  if (sync) { this.sync(sync); this.open-- }
  else if (before) { this.marks = before }
  return true
};

// : (dom.Node, ?NodeBuilder, ?number, ?number)
// Add all child nodes between `startIndex` and `endIndex` (or the
// whole node, if not given). If `sync` is passed, use it to
// synchronize after every block element.
ParseContext.prototype.addAll = function (parent, sync, startIndex, endIndex) {
    var this$1 = this;

  var index = startIndex || 0
  for (var dom = startIndex ? parent.childNodes[startIndex] : parent.firstChild,
           end = endIndex == null ? null : parent.childNodes[endIndex];
       dom != end; dom = dom.nextSibling, ++index) {
    this$1.findAtPoint(parent, index)
    this$1.addDOM(dom)
    if (sync && blockTags.hasOwnProperty(dom.nodeName.toLowerCase()))
      { this$1.sync(sync) }
  }
  this.findAtPoint(parent, index)
};

// Try to find a way to fit the given node type into the current
// context. May add intermediate wrappers and/or leave non-solid
// nodes that we're in.
ParseContext.prototype.findPlace = function (type, attrs) {
    var this$1 = this;

  var route, sync
  for (var depth = this.open; depth >= 0; depth--) {
    var node = this$1.nodes[depth]
    var found = node.findWrapping(type, attrs)
    if (found && (!route || route.length > found.length)) {
      route = found
      sync = node
      if (!found.length) { break }
    }
    if (node.solid) { break }
  }
  if (!route) { return false }
  this.sync(sync)
  for (var i = 0; i < route.length; i++)
    { this$1.enterInner(route[i].type, route[i].attrs, false) }
  return true
};

// : (Node) → ?Node
// Try to insert the given node, adjusting the context when needed.
ParseContext.prototype.insertNode = function (node) {
  if (this.findPlace(node.type, node.attrs)) {
    this.closeExtra()
    var top = this.top
    if (top.match) {
      var match = top.match.matchNode(node)
      if (!match) {
        node = node.mark(node.marks.filter(function (mark) { return top.match.allowsMark(mark.type); }))
        match = top.match.matchNode(node)
      }
      top.match = match
    }
    top.content.push(node)
  }
};

// : (NodeType, ?Object) → bool
// Try to start a node of the given type, adjusting the context when
// necessary.
ParseContext.prototype.enter = function (type, attrs, preserveWS) {
  var ok = this.findPlace(type, attrs)
  if (ok) { this.enterInner(type, attrs, true, preserveWS) }
  return ok
};

// Open a node of the given type
ParseContext.prototype.enterInner = function (type, attrs, solid, preserveWS) {
  this.closeExtra()
  var top = this.top
  top.match = top.match && top.match.matchType(type, attrs)
  var options = preserveWS == null ? top.options & ~OPT_OPEN_LEFT : wsOptionsFor(preserveWS)
  if ((top.options & OPT_OPEN_LEFT) && top.content.length == 0) { options |= OPT_OPEN_LEFT }
  this.nodes.push(new NodeContext(type, attrs, solid, null, options))
  this.open++
};

// Make sure all nodes above this.open are finished and added to
// their parents
ParseContext.prototype.closeExtra = function (openRight) {
    var this$1 = this;

  var i = this.nodes.length - 1
  if (i > this.open) {
    this.marks = Mark.none
    for (; i > this.open; i--) { this$1.nodes[i - 1].content.push(this$1.nodes[i].finish(openRight)) }
    this.nodes.length = this.open + 1
  }
};

ParseContext.prototype.finish = function () {
  this.open = 0
  this.closeExtra(this.isOpen)
  return this.nodes[0].finish(this.isOpen || this.options.topOpen)
};

ParseContext.prototype.sync = function (to) {
    var this$1 = this;

  for (var i = this.open; i >= 0; i--) { if (this$1.nodes[i] == to) {
    this$1.open = i
    return
  } }
};

prototypeAccessors.currentPos.get = function () {
    var this$1 = this;

  this.closeExtra()
  var pos = 0
  for (var i = this.open; i >= 0; i--) {
    var content = this$1.nodes[i].content
    for (var j = content.length - 1; j >= 0; j--)
      { pos += content[j].nodeSize }
    if (i) { pos++ }
  }
  return pos
};

ParseContext.prototype.findAtPoint = function (parent, offset) {
    var this$1 = this;

  if (this.find) { for (var i = 0; i < this.find.length; i++) {
    if (this$1.find[i].node == parent && this$1.find[i].offset == offset)
      { this$1.find[i].pos = this$1.currentPos }
  } }
};

ParseContext.prototype.findInside = function (parent) {
    var this$1 = this;

  if (this.find) { for (var i = 0; i < this.find.length; i++) {
    if (this$1.find[i].pos == null && parent.nodeType == 1 && parent.contains(this$1.find[i].node))
      { this$1.find[i].pos = this$1.currentPos }
  } }
};

ParseContext.prototype.findAround = function (parent, content, before) {
    var this$1 = this;

  if (parent != content && this.find) { for (var i = 0; i < this.find.length; i++) {
    if (this$1.find[i].pos == null && parent.nodeType == 1 && parent.contains(this$1.find[i].node)) {
      var pos = content.compareDocumentPosition(this$1.find[i].node)
      if (pos & (before ? 2 : 4))
        { this$1.find[i].pos = this$1.currentPos }
    }
  } }
};

ParseContext.prototype.findInText = function (textNode) {
    var this$1 = this;

  if (this.find) { for (var i = 0; i < this.find.length; i++) {
    if (this$1.find[i].node == textNode)
      { this$1.find[i].pos = this$1.currentPos - (textNode.nodeValue.length - this$1.find[i].offset) }
  } }
};

// : (string) → bool
// Determines whether the given [context
// string](##ParseRule.context) matches this context.
ParseContext.prototype.matchesContext = function (context) {
    var this$1 = this;

  var parts = context.split("/")
  var option = this.options.context
  var useRoot = !this.isOpen && (!option || option.parent.type == this.nodes[0].type)
  var minDepth = -(option ? option.depth + 1 : 0) + (useRoot ? 0 : 1)
  var match = function (i, depth) {
    for (; i >= 0; i--) {
      var part = parts[i]
      if (part == "") {
        if (i == parts.length - 1 || i == 0) { continue }
        for (; depth >= minDepth; depth--)
          { if (match(i - 1, depth)) { return true } }
        return false
      } else {
        var next = depth > 0 || (depth == 0 && useRoot) ? this$1.nodes[depth].type
            : option && depth >= minDepth ? option.node(depth - minDepth).type
            : null
        if (!next || (next.name != part && next.groups.indexOf(part) == -1))
          { return false }
        depth--
      }
    }
    return true
  }
  return match(parts.length - 1, this.open)
};

Object.defineProperties( ParseContext.prototype, prototypeAccessors );

// Kludge to work around directly nested list nodes produced by some
// tools and allowed by browsers to mean that the nested list is
// actually part of the list item above it.
function normalizeList(dom) {
  for (var child = dom.firstChild, prevItem = null; child; child = child.nextSibling) {
    var name = child.nodeType == 1 ? child.nodeName.toLowerCase() : null
    if (name && listTags.hasOwnProperty(name) && prevItem) {
      prevItem.appendChild(child)
      child = prevItem
    } else if (name == "li") {
      prevItem = child
    } else if (name) {
      prevItem = null
    }
  }
}

// Apply a CSS selector.
function matches(dom, selector) {
  return (dom.matches || dom.msMatchesSelector || dom.webkitMatchesSelector || dom.mozMatchesSelector).call(dom, selector)
}

// : (string) → [string]
// Tokenize a style attribute into property/value pairs.
function parseStyles(style) {
  var re = /\s*([\w-]+)\s*:\s*([^;]+)/g, m, result = []
  while (m = re.exec(style)) { result.push(m[1], m[2].trim()) }
  return result
}

function copy(obj) {
  var copy = {}
  for (var prop in obj) { copy[prop] = obj[prop] }
  return copy
}


/***/ }),
/* 35 */
/***/ (function(module, exports, __webpack_require__) {

var OrderedMap = __webpack_require__(30)

var ref = __webpack_require__(16);
var Node = ref.Node;
var TextNode = ref.TextNode;
var ref$1 = __webpack_require__(3);
var Fragment = ref$1.Fragment;
var ref$2 = __webpack_require__(4);
var Mark = ref$2.Mark;
var ref$3 = __webpack_require__(15);
var ContentExpr = ref$3.ContentExpr;

// For node types where all attrs have a default value (or which don't
// have any attributes), build up a single reusable default attribute
// object, and use it for all nodes that don't specify specific
// attributes.
function defaultAttrs(attrs) {
  var defaults = Object.create(null)
  for (var attrName in attrs) {
    var attr = attrs[attrName]
    if (attr.default === undefined) { return null }
    defaults[attrName] = attr.default
  }
  return defaults
}

function computeAttrs(attrs, value) {
  var built = Object.create(null)
  for (var name in attrs) {
    var given = value && value[name]
    if (given == null) {
      var attr = attrs[name]
      if (attr.default !== undefined)
        { given = attr.default }
      else if (attr.compute)
        { given = attr.compute() }
      else
        { throw new RangeError("No value supplied for attribute " + name) }
    }
    built[name] = given
  }
  return built
}

function initAttrs(attrs) {
  var result = Object.create(null)
  if (attrs) { for (var name in attrs) { result[name] = new Attribute(attrs[name]) } }
  return result
}

// ::- Node types are objects allocated once per `Schema` and used to
// tag `Node` instances with a type. They contain information about
// the node type, such as its name and what kind of node it
// represents.
var NodeType = function(name, schema, spec) {
  // :: string
  // The name the node type has in this schema.
  this.name = name

  // :: Schema
  // A link back to the `Schema` the node type belongs to.
  this.schema = schema

  // :: NodeSpec
  // The spec that this type is based on
  this.spec = spec

  this.groups = spec.group ? spec.group.split(" ") : []
  this.attrs = initAttrs(spec.attrs)

  this.defaultAttrs = defaultAttrs(this.attrs)
  this.contentExpr = null

  // :: bool
  // True if this is a block type
  this.isBlock = !(spec.inline || name == "text")

  // :: bool
  // True if this is the text node type.
  this.isText = name == "text"
};

var prototypeAccessors = { isInline: {},isTextblock: {},inlineContent: {},isLeaf: {},isAtom: {} };

// :: bool
// True if this is an inline type.
prototypeAccessors.isInline.get = function () { return !this.isBlock };

// :: bool
// True if this is a textblock type, a block that contains inline
// content.
prototypeAccessors.isTextblock.get = function () { return this.isBlock && this.contentExpr.inlineContent };

// :: bool
// True if this node type has inline content.
prototypeAccessors.inlineContent.get = function () { return this.contentExpr.inlineContent };

// :: bool
// True for node types that allow no content.
prototypeAccessors.isLeaf.get = function () { return this.contentExpr.isLeaf };

// :: bool
// True when this node is an atom, i.e. when it does not have
// directly editable content.
prototypeAccessors.isAtom.get = function () { return this.isLeaf || this.spec.atom };

NodeType.prototype.hasRequiredAttrs = function (ignore) {
    var this$1 = this;

  for (var n in this$1.attrs)
    { if (this$1.attrs[n].isRequired && (!ignore || !(n in ignore))) { return true } }
  return false
};

NodeType.prototype.compatibleContent = function (other) {
  return this == other || this.contentExpr.compatible(other.contentExpr)
};

NodeType.prototype.computeAttrs = function (attrs) {
  if (!attrs && this.defaultAttrs) { return this.defaultAttrs }
  else { return computeAttrs(this.attrs, attrs) }
};

// :: (?Object, ?union<Fragment, Node, [Node]>, ?[Mark]) → Node
// Create a `Node` of this type. The given attributes are
// checked and defaulted (you can pass `null` to use the type's
// defaults entirely, if no required attributes exist). `content`
// may be a `Fragment`, a node, an array of nodes, or
// `null`. Similarly `marks` may be `null` to default to the empty
// set of marks.
NodeType.prototype.create = function (attrs, content, marks) {
  if (typeof content == "string") { throw new Error("Calling create with string") }
  if (this.isText) { throw new Error("NodeType.create can't construct text nodes") }
  return new Node(this, this.computeAttrs(attrs), Fragment.from(content), Mark.setFrom(marks))
};

// :: (?Object, ?union<Fragment, Node, [Node]>, ?[Mark]) → Node
// Like [`create`](#model.NodeType.create), but check the given content
// against the node type's content restrictions, and throw an error
// if it doesn't match.
NodeType.prototype.createChecked = function (attrs, content, marks) {
  attrs = this.computeAttrs(attrs)
  content = Fragment.from(content)
  if (!this.validContent(content, attrs))
    { throw new RangeError("Invalid content for node " + this.name) }
  return new Node(this, attrs, content, Mark.setFrom(marks))
};

// :: (?Object, ?union<Fragment, Node, [Node]>, ?[Mark]) → ?Node
// Like [`create`](#model.NodeType.create), but see if it is necessary to
// add nodes to the start or end of the given fragment to make it
// fit the node. If no fitting wrapping can be found, return null.
// Note that, due to the fact that required nodes can always be
// created, this will always succeed if you pass null or
// `Fragment.empty` as content.
NodeType.prototype.createAndFill = function (attrs, content, marks) {
  attrs = this.computeAttrs(attrs)
  content = Fragment.from(content)
  if (content.size) {
    var before = this.contentExpr.start(attrs).fillBefore(content)
    if (!before) { return null }
    content = before.append(content)
  }
  var after = this.contentExpr.getMatchAt(attrs, content).fillBefore(Fragment.empty, true)
  if (!after) { return null }
  return new Node(this, attrs, content.append(after), Mark.setFrom(marks))
};

// :: (Fragment, ?Object) → bool
// Returns true if the given fragment is valid content for this node
// type with the given attributes.
NodeType.prototype.validContent = function (content, attrs) {
  return this.contentExpr.matches(attrs, content)
};

NodeType.compile = function (nodes, schema) {
  var result = Object.create(null)
  nodes.forEach(function (name, spec) { return result[name] = new NodeType(name, schema, spec); })

  var topType = schema.spec.topNode || "doc"
  if (!result[topType]) { throw new RangeError("Schema is missing its top node type ('" + topType + "')") }
  if (!result.text) { throw new RangeError("Every schema needs a 'text' type") }
  for (var _ in result.text.attrs) { throw new RangeError("The text node type should not have attributes") }

  return result
};

Object.defineProperties( NodeType.prototype, prototypeAccessors );
exports.NodeType = NodeType

// Attribute descriptors

var Attribute = function(options) {
  this.default = options.default
  this.compute = options.compute
};

var prototypeAccessors$1 = { isRequired: {} };

prototypeAccessors$1.isRequired.get = function () {
  return this.default === undefined && !this.compute
};

Object.defineProperties( Attribute.prototype, prototypeAccessors$1 );

// Marks

// ::- Like nodes, marks (which are associated with nodes to signify
// things like emphasis or being part of a link) are tagged with type
// objects, which are instantiated once per `Schema`.
var MarkType = function(name, rank, schema, spec) {
  // :: string
  // The name of the mark type.
  this.name = name

  // :: Schema
  // The schema that this mark type instance is part of.
  this.schema = schema

  // :: MarkSpec
  // The spec on which the type is based.
  this.spec = spec

  this.attrs = initAttrs(spec.attrs)

  this.rank = rank
  this.excluded = null
  var defaults = defaultAttrs(this.attrs)
  this.instance = defaults && new Mark(this, defaults)
};

// :: (?Object) → Mark
// Create a mark of this type. `attrs` may be `null` or an object
// containing only some of the mark's attributes. The others, if
// they have defaults, will be added.
MarkType.prototype.create = function (attrs) {
  if (!attrs && this.instance) { return this.instance }
  return new Mark(this, computeAttrs(this.attrs, attrs))
};

MarkType.compile = function (marks, schema) {
  var result = Object.create(null), rank = 0
  marks.forEach(function (name, spec) { return result[name] = new MarkType(name, rank++, schema, spec); })
  return result
};

// :: ([Mark]) → [Mark]
// When there is a mark of this type in the given set, a new set
// without it is returned. Otherwise, the input set is returned.
MarkType.prototype.removeFromSet = function (set) {
    var this$1 = this;

  for (var i = 0; i < set.length; i++)
    { if (set[i].type == this$1)
      { return set.slice(0, i).concat(set.slice(i + 1)) } }
  return set
};

// :: ([Mark]) → ?Mark
// Tests whether there is a mark of this type in the given set.
MarkType.prototype.isInSet = function (set) {
    var this$1 = this;

  for (var i = 0; i < set.length; i++)
    { if (set[i].type == this$1) { return set[i] } }
};

// :: MarkType → bool
MarkType.prototype.excludes = function (other) {
  return this.excluded.indexOf(other) > -1
};
exports.MarkType = MarkType

// SchemaSpec:: interface
// An object describing a schema, as passed to the `Schema`
// constructor.
//
//   nodes:: union<Object<NodeSpec>, OrderedMap<NodeSpec>>
//   The node types in this schema. Maps names to `NodeSpec` objects
//   describing the node to be associated with that name. Their order
//   is significant
//
//   marks:: ?union<Object<MarkSpec>, OrderedMap<MarkSpec>>
//   The mark types that exist in this schema.
//
//   topNode:: ?string
//   The name of the default top-level node for the schema. Defaults
//   to `"doc"`.

// NodeSpec:: interface
//
//   content:: ?string
//   The content expression for this node, as described in the [schema
//   guide](/docs/guides/schema/). When not given, the node does not allow
//   any content.
//
//   group:: ?string
//   The group or space-separated groups to which this node belongs, as
//   referred to in the content expressions for the schema.
//
//   inline:: ?bool
//   Should be set to a truthy value for inline nodes. (Implied for
//   text nodes.)
//
//   atom:: ?bool
//   Can be set to true to indicate that, though this isn't a [leaf
//   node](#model.NodeType.isLeaf), it doesn't have directly editable
//   content and should be treated as a single unit in the view.
//
//   attrs:: ?Object<AttributeSpec>
//   The attributes that nodes of this type get.
//
//   selectable:: ?bool
//   Controls whether nodes of this type can be selected (as a [node
//   selection](#state.NodeSelection)). Defaults to true for non-text
//   nodes.
//
//   draggable:: ?bool
//   Determines whether nodes of this type can be dragged without
//   being selected. Defaults to false.
//
//   code:: ?bool
//   Can be used to indicate that this node contains code, which
//   causes some commands to behave differently.
//
//   defining:: ?bool
//   Determines whether this node is considered an important parent
//   node during replace operations (such as paste). Non-defining (the
//   default) nodes get dropped when their entire content is replaced,
//   whereas defining nodes persist and wrap the inserted content.
//   Likewise, the the _inserted_ content, when not inserting into a
//   textblock, the defining parents of the content are preserved.
//   Typically, non-default-paragraph textblock types, and possible
//   list items, are marked as defining.
//
//   toDOM:: ?(Node) → DOMOutputSpec
//   Defines the default way a node of this type should be serialized
//   to DOM/HTML (as used by
//   [`DOMSerializer.fromSchema`](#model.DOMSerializer^fromSchema)).
//   Should return an [array structure](#model.DOMOutputSpec) that
//   describes the resulting DOM structure, with an optional number
//   zero (“hole”) in it to indicate where the node's content should
//   be inserted.
//
//   For text nodes, the default is to create a text DOM node. Though
//   it is possible to create a serializer where text is rendered
//   differently, this is not supported inside the editor, so you
//   shouldn't override that in your text node spec.
//
//   parseDOM:: ?[ParseRule]
//   Associates DOM parser information with this node, which can be
//   used by [`DOMParser.fromSchema`](#model.DOMParser^fromSchema) to
//   automatically derive a parser. The `node` field in the rules is
//   implied (the name of this node will be filled in automatically).
//   If you supply your own parser, you do not need to also specify
//   parsing rules in your schema.

// MarkSpec:: interface
//
//   attrs:: ?Object<AttributeSpec>
//   The attributes that marks of this type get.
//
//   inclusive:: ?bool
//   Whether this mark should be active when the cursor is positioned
//   at the start or end boundary of the mark. Defaults to true.
//
//   excludes:: ?string
//   Determines which other marks this mark can coexist with. Should
//   be a space-separated strings naming other marks or groups of marks.
//   When a mark is [added](#model.mark.addToSet) to a set, all marks
//   that it excludes are removed in the process. If the set contains
//   any mark that excludes the new mark but is not, itself, excluded
//   by the new mark, the mark can not be added an the set. You can
//   use the value `"_"` to indicate that the mark excludes all
//   marks in the schema.
//
//   Defaults to only being exclusive with marks of the same type. You
//   can set it to an empty string (or any string not containing the
//   mark's own name) to allow multiple marks of a given type to
//   coexist (as long as they have different attributes).
//
//   group:: ?string
//   The group or space-separated groups to which this node belongs.
//
//   toDOM:: ?(mark: Mark) → DOMOutputSpec
//   Defines the default way marks of this type should be serialized
//   to DOM/HTML.
//
//   parseDOM:: ?[ParseRule]
//   Associates DOM parser information with this mark (see the
//   corresponding [node spec field](#model.NodeSpec.parseDOM)). The
//   `mark` field in the rules is implied.

// AttributeSpec:: interface
//
// Used to define attributes. Attributes that have no default or
// compute property must be provided whenever a node or mark of a type
// that has them is created.
//
// The following fields are supported:
//
//   default:: ?any
//   The default value for this attribute, to choose when no
//   explicit value is provided.
//
//   compute:: ?() → any
//   A function that computes a default value for the attribute.

// ::- A document schema.
var Schema = function(spec) {
  var this$1 = this;

  // :: SchemaSpec
  // The [spec](#model.SchemaSpec) on which the schema is based,
  // with the added guarantee that its `nodes` and `marks`
  // properties are
  // [`OrderedMap`](https://github.com/marijnh/orderedmap) instances
  // (not raw objects or null).
  this.spec = {}
  for (var prop in spec) { this$1.spec[prop] = spec[prop] }
  this.spec.nodes = OrderedMap.from(spec.nodes)
  this.spec.marks = OrderedMap.from(spec.marks)

  // :: Object<NodeType>
  // An object mapping the schema's node names to node type objects.
  this.nodes = NodeType.compile(this.spec.nodes, this)

  // :: Object<MarkType>
  // A map from mark names to mark type objects.
  this.marks = MarkType.compile(this.spec.marks, this)

  for (var prop$1 in this$1.nodes) {
    if (prop$1 in this$1.marks)
      { throw new RangeError(prop$1 + " can not be both a node and a mark") }
    var type = this$1.nodes[prop$1]
    type.contentExpr = ContentExpr.parse(type, this$1.spec.nodes.get(prop$1).content || "")
  }
  for (var prop$2 in this$1.marks) {
    var type$1 = this$1.marks[prop$2], excl = type$1.spec.excludes
    type$1.excluded = excl == null ? [type$1] : excl == "" ? [] : ContentExpr.gatherMarks(this$1, excl.split(" "))
  }

  // :: Object
  // An object for storing whatever values modules may want to
  // compute and cache per schema. (If you want to store something
  // in it, try to use property names unlikely to clash.)
  this.cached = Object.create(null)
  this.cached.wrappings = Object.create(null)

  this.nodeFromJSON = this.nodeFromJSON.bind(this)
  this.markFromJSON = this.markFromJSON.bind(this)

  // :: NodeType
  // The type of the [default top node](#model.SchemaSpec.topNode)
  // for this schema.
  this.topNodeType = this.nodes[this.spec.topNode || "doc"]
};

// :: (union<string, NodeType>, ?Object, ?union<Fragment, Node, [Node]>, ?[Mark]) → Node
// Create a node in this schema. The `type` may be a string or a
// `NodeType` instance. Attributes will be extended
// with defaults, `content` may be a `Fragment`,
// `null`, a `Node`, or an array of nodes.
Schema.prototype.node = function (type, attrs, content, marks) {
  if (typeof type == "string")
    { type = this.nodeType(type) }
  else if (!(type instanceof NodeType))
    { throw new RangeError("Invalid node type: " + type) }
  else if (type.schema != this)
    { throw new RangeError("Node type from different schema used (" + type.name + ")") }

  return type.createChecked(attrs, content, marks)
};

// :: (string, ?[Mark]) → Node
// Create a text node in the schema. Empty text nodes are not
// allowed.
Schema.prototype.text = function (text$1, marks) {
  var type = this.nodes.text
  return new TextNode(type, type.defaultAttrs, text$1, Mark.setFrom(marks))
};

// :: (union<string, MarkType>, ?Object) → Mark
// Create a mark with the given type and attributes.
Schema.prototype.mark = function (type, attrs) {
  if (typeof type == "string") { type = this.marks[type] }
  return type.create(attrs)
};

// :: (Object) → Node
// Deserialize a node from its JSON representation. This method is
// bound.
Schema.prototype.nodeFromJSON = function (json) {
  return Node.fromJSON(this, json)
};

// :: (Object) → Mark
// Deserialize a mark from its JSON representation. This method is
// bound.
Schema.prototype.markFromJSON = function (json) {
  return Mark.fromJSON(this, json)
};

Schema.prototype.nodeType = function (name) {
  var found = this.nodes[name]
  if (!found) { throw new RangeError("Unknown node type: " + name) }
  return found
};
exports.Schema = Schema


/***/ }),
/* 36 */
/***/ (function(module, exports) {

// DOMOutputSpec:: interface
// A description of a DOM structure. Can be either a string, which is
// interpreted as a text node, a DOM node, which is interpreted as
// itself, or an array.
//
// An array describes a DOM element. The first element in the array
// should be a string, and is the name of the DOM element. If the
// second element is a non-Array, non-DOM node object, it is
// interpreted as an object providing the DOM element's attributes.
// Any elements after that (including the 2nd if it's not an attribute
// object) are interpreted as children of the DOM elements, and must
// either be valid `DOMOutputSpec` values, or the number zero.
//
// The number zero (pronounced “hole”) is used to indicate the place
// where a ProseMirror node's content should be inserted.

// ::- A DOM serializer knows how to convert ProseMirror nodes and
// marks of various types to DOM nodes.
var DOMSerializer = function(nodes, marks) {
  // :: Object<(node: Node) → DOMOutputSpec>
  this.nodes = nodes || {}
  // :: Object<(mark: Mark) → DOMOutputSpec>
  this.marks = marks || {}
};

// :: (Fragment, ?Object) → dom.DocumentFragment
// Serialize the content of this fragment to a DOM fragment. When
// not in the browser, the `document` option, containing a DOM
// document, should be passed so that the serializer can create
// nodes.
DOMSerializer.prototype.serializeFragment = function (fragment, options, target) {
    var this$1 = this;
    if ( options === void 0 ) options = {};

  if (!target) { target = doc(options).createDocumentFragment() }

  var top = target, active = null
  fragment.forEach(function (node) {
    if (active || node.marks.length) {
      if (!active) { active = [] }
      var keep = 0
      for (; keep < Math.min(active.length, node.marks.length); ++keep)
        { if (!node.marks[keep].eq(active[keep])) { break } }
      while (keep < active.length) {
        active.pop()
        top = top.parentNode
      }
      while (active.length < node.marks.length) {
        var add = node.marks[active.length]
        active.push(add)
        top = top.appendChild(this$1.serializeMark(add, options))
      }
    }
    top.appendChild(this$1.serializeNode(node, options))
  })

  return target
};

// :: (Node, ?Object) → dom.Node
// Serialize this node to a DOM node. This can be useful when you
// need to serialize a part of a document, as opposed to the whole
// document. To serialize a whole document, use
// [`serializeFragment`](#model.DOMSerializer.serializeFragment) on
// its [`content`](#model.Node.content).
DOMSerializer.prototype.serializeNode = function (node, options) {
    if ( options === void 0 ) options = {};

  return this.renderStructure(this.nodes[node.type.name](node), node, options)
};

DOMSerializer.prototype.serializeNodeAndMarks = function (node, options) {
    var this$1 = this;
    if ( options === void 0 ) options = {};

  var dom = this.serializeNode(node, options)
  for (var i = node.marks.length - 1; i >= 0; i--) {
    var wrap = this$1.serializeMark(node.marks[i], options)
    wrap.appendChild(dom)
    dom = wrap
  }
  return dom
};

DOMSerializer.prototype.serializeMark = function (mark, options) {
    if ( options === void 0 ) options = {};

  return this.renderStructure(this.marks[mark.type.name](mark), null, options)
};

// :: (dom.Document, DOMOutputSpec) → {dom: dom.Node, contentDOM: ?dom.Node}
// Render an [output spec](##model.DOMOutputSpec).
DOMSerializer.renderSpec = function (doc, structure) {
  if (typeof structure == "string")
    { return {dom: doc.createTextNode(structure)} }
  if (structure.nodeType != null)
    { return {dom: structure} }
  var dom = doc.createElement(structure[0]), contentDOM = null
  var attrs = structure[1], start = 1
  if (attrs && typeof attrs == "object" && attrs.nodeType == null && !Array.isArray(attrs)) {
    start = 2
    for (var name in attrs) {
      if (name == "style") { dom.style.cssText = attrs[name] }
      else if (attrs[name] != null) { dom.setAttribute(name, attrs[name]) }
    }
  }
  for (var i = start; i < structure.length; i++) {
    var child = structure[i]
    if (child === 0) {
      if (i < structure.length - 1 || i > start)
        { throw new RangeError("Content hole must be the only child of its parent node") }
      return {dom: dom, contentDOM: dom}
    } else {
      var ref = DOMSerializer.renderSpec(doc, child);
        var inner = ref.dom;
        var innerContent = ref.contentDOM;
      dom.appendChild(inner)
      if (innerContent) {
        if (contentDOM) { throw new RangeError("Multiple content holes") }
        contentDOM = innerContent
      }
    }
  }
  return {dom: dom, contentDOM: contentDOM}
};

DOMSerializer.prototype.renderStructure = function (structure, node, options) {
  var ref = DOMSerializer.renderSpec(doc(options), structure);
    var dom = ref.dom;
    var contentDOM = ref.contentDOM;
  if (contentDOM) {
    if (!node || node.isLeaf)
      { throw new RangeError("Content hole not allowed in a mark or leaf node spec") }
    if (options.onContent)
      { options.onContent(node, contentDOM, options) }
    else
      { this.serializeFragment(node.content, options, contentDOM) }
  }
  return dom
};

// :: (Schema) → DOMSerializer
// Build a serializer using the [`toDOM`](#model.NodeSpec.toDOM)
// properties in a schema's node and mark specs.
DOMSerializer.fromSchema = function (schema) {
  return schema.cached.domSerializer ||
    (schema.cached.domSerializer = new DOMSerializer(this.nodesFromSchema(schema), this.marksFromSchema(schema)))
};

// :: (Schema) → Object<(node: Node) → DOMOutputSpec>
// Gather the serializers in a schema's node specs into an object.
// This can be useful as a base to build a custom serializer from.
DOMSerializer.nodesFromSchema = function (schema) {
  var result = gatherToDOM(schema.nodes)
  if (!result.text) { result.text = function (node) { return node.text; } }
  return result
};

// :: (Schema) → Object<(mark: Mark) → DOMOutputSpec>
// Gather the serializers in a schema's mark specs into an object.
DOMSerializer.marksFromSchema = function (schema) {
  return gatherToDOM(schema.marks)
};
exports.DOMSerializer = DOMSerializer

function gatherToDOM(obj) {
  var result = {}
  for (var name in obj) {
    var toDOM = obj[name].spec.toDOM
    if (toDOM) { result[name] = toDOM }
  }
  return result
}

function doc(options) {
  // declare global: window
  return options.document || window.document
}


/***/ }),
/* 37 */
/***/ (function(module, exports) {

// PluginSpec:: Object
// A plugin spec provides a definition for a plugin.
//
//   props:: ?EditorProps
//   The [view props](#view.EditorProps) added by this plugin.
//   Note that the
//   [`dispatchTransaction`](#view.EditorProps.dispatchTransaction)
//   and [`state`](#view.EditorProps.state) props can't be defined
//   by plugins, only by the main props object. Props that are
//   functions will be bound to have the plugin instance as their
//   `this` binding.
//
//   state:: ?StateField
//   A [state field](#state.StateField) defined by this plugin.
//
//   key:: ?PluginKey
//   Can optionally be used to make this a keyed plugin. You can
//   have only one plugin with a given key in a given state, but
//   it is possible to access the plugin's configuration and state
//   through the key, without having access to the plugin instance
//   itself.
//
//   view:: ?(EditorView) → Object
//   When the plugin needs to interact with the editor view, or
//   set something up in the DOM, use this field. The function
//   will be called when the plugin's state is associated with an
//   editor view.
//
//     return::-
//     Should return an object with the following optional
//     properties:
//
//       update:: ?(view: EditorView, prevState: EditorState)
//       Called whenever the view's state is updated.
//
//       destroy:: ?()
//       Called when the view is destroyed or receives a state
//       with different plugins.
//
//   filterTransaction:: ?(Transaction, EditorState) → bool
//   When present, this will be called before a transaction is
//   applied by the state, allowing the plugin to cancel it (by
//   returning false).
//
//   appendTransaction:: ?(transactions: [Transaction], oldState: EditorState, newState: EditorState) → ?Transaction
//   Allows the plugin to append another transaction to be applied
//   after the given array of transactions. When another plugin
//   appends a transaction after this was called, it is called
//   again with the new state and extended array of transactions.

function bindProps(obj, self, target) {
  for (var prop in obj) {
    var val = obj[prop]
    if (val instanceof Function) { val = val.bind(self) }
    else if (prop == "handleDOMEvents") { val = bindProps(val, self, {}) }
    target[prop] = val
  }
  return target
}

// ::- Plugins wrap extra functionality that can be added to an
// editor. They can define new [state fields](#state.StateField), and
// add [view props](#view.EditorProps).
var Plugin = function Plugin(spec) {
  // :: EditorProps
  // The props exported by this plugin.
  this.props = {}
  if (spec.props) { bindProps(spec.props, this, this.props) }
  // :: Object
  // The plugin's configuration object.
  this.spec = spec
  this.key = spec.key ? spec.key.key : createKey("plugin")
};

// :: (EditorState) → any
// Get the state field for this plugin.
Plugin.prototype.getState = function getState (state) { return state[this.key] };
exports.Plugin = Plugin

// StateField:: interface<T>
// A plugin may provide a state field (under its `state` property) of
// this type, which describes the state it wants to keep. Functions
// provided here are always called with the plugin instance as their
// `this` binding.
//
//   init:: (config: Object, instance: EditorState) → T
//   Initialize the value of this field. `config` will be the object
//   passed to [`EditorState.create`](#state.EditorState^create). Note
//   that `instance` is a half-initialized state instance, and will
//   not have values for any fields initialized after this one.
//
//   apply:: (tr: Transaction, value: T, oldState: EditorState, newState: EditorState) → T
//   Apply the given transaction to this state field, producing a new
//   field value. Note that the `newState` argument is a partially
//   constructed state does not yet contain the state from plugins
//   coming after this plugin.
//
//   toJSON:: ?(value: T) → *
//   Convert this field to JSON. Optional, can be left off to disable
//   JSON serialization for the field.
//
//   fromJSON:: ?(config: Object, value: *, state: EditorState) → T
//   Deserialize the JSON representation of this field. Note that the
//   `state` argument is again a half-initialized state.

var keys = Object.create(null)

function createKey(name) {
  if (name in keys) { return name + "$" + ++keys[name] }
  keys[name] = 0
  return name + "$"
}

// ::- A key is used to [tag](#state.PluginSpec.key)
// plugins in a way that makes it possible to find them, given an
// editor state. Assigning a key does mean only one plugin of that
// type can be active in a state.
var PluginKey = function PluginKey(name) {
if ( name === void 0 ) name = "key";
 this.key = createKey(name) };

// :: (EditorState) → ?Plugin
// Get the active plugin with this key, if any, from an editor
// state.
PluginKey.prototype.get = function get (state) { return state.config.pluginsByKey[this.key] };

// :: (EditorState) → ?any
// Get the plugin's state from an editor state.
PluginKey.prototype.getState = function getState (state) { return state[this.key] };
exports.PluginKey = PluginKey


/***/ }),
/* 38 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Node = ref.Node;

var ref$1 = __webpack_require__(18);
var Selection = ref$1.Selection;
var ref$2 = __webpack_require__(19);
var Transaction = ref$2.Transaction;

function bind(f, self) {
  return !self || !f ? f : f.bind(self)
}

var FieldDesc = function FieldDesc(name, desc, self) {
  this.name = name
  this.init = bind(desc.init, self)
  this.apply = bind(desc.apply, self)
};

var baseFields = [
  new FieldDesc("doc", {
    init: function init(config) { return config.doc || config.schema.topNodeType.createAndFill() },
    apply: function apply(tr) { return tr.doc }
  }),

  new FieldDesc("selection", {
    init: function init(config, instance) { return config.selection || Selection.atStart(instance.doc) },
    apply: function apply(tr) { return tr.selection }
  }),

  new FieldDesc("storedMarks", {
    init: function init() { return null },
    apply: function apply(tr, _marks, _old, state) { return state.selection.$cursor ? tr.storedMarks : null }
  }),

  new FieldDesc("scrollToSelection", {
    init: function init() { return 0 },
    apply: function apply(tr, prev) { return tr.scrolledIntoView ? prev + 1 : prev }
  })
]

// Object wrapping the part of a state object that stays the same
// across transactions. Stored in the state's `config` property.
var Configuration = function Configuration(schema, plugins) {
  var this$1 = this;

  this.schema = schema
  this.fields = baseFields.concat()
  this.plugins = []
  this.pluginsByKey = Object.create(null)
  if (plugins) { plugins.forEach(function (plugin) {
    if (this$1.pluginsByKey[plugin.key])
      { throw new RangeError("Adding different instances of a keyed plugin (" + plugin.key + ")") }
    this$1.plugins.push(plugin)
    this$1.pluginsByKey[plugin.key] = plugin
    if (plugin.spec.state)
      { this$1.fields.push(new FieldDesc(plugin.key, plugin.spec.state, plugin)) }
  }) }
};

// ::- The state of a ProseMirror editor is represented by an object
// of this type. This is a persistent data structure—it isn't updated,
// but rather a new state value is computed from an old one with the
// [`apply`](#state.EditorState.apply) method.
//
// In addition to the built-in state fields, plugins can define
// additional pieces of state.
var EditorState = function EditorState(config) {
  this.config = config
};

var prototypeAccessors = { schema: {},plugins: {},tr: {} };

// doc:: Node
// The current document.

// selection:: Selection
// The selection.

// storedMarks:: ?[Mark]
// A set of marks to apply to the next character that's typed. Will
// be null whenever no explicit marks have been set.

// :: Schema
// The schema of the state's document.
prototypeAccessors.schema.get = function () {
  return this.config.schema
};

// :: [Plugin]
// The plugins that are active in this state.
prototypeAccessors.plugins.get = function () {
  return this.config.plugins
};

// :: (Transaction) → EditorState
// Apply the given transaction to produce a new state.
EditorState.prototype.apply = function apply (tr) {
  return this.applyTransaction(tr).state
};

// : (Transaction) → ?Transaction
EditorState.prototype.filterTransaction = function filterTransaction (tr, ignore) {
    var this$1 = this;
    if ( ignore === void 0 ) ignore = -1;

  for (var i = 0; i < this.config.plugins.length; i++) { if (i != ignore) {
    var plugin = this$1.config.plugins[i]
    if (plugin.spec.filterTransaction && !plugin.spec.filterTransaction.call(plugin, tr, this$1))
      { return false }
  } }
  return true
};

// :: (Transaction) → {state: EditorState, transactions: [Transaction]}
// Verbose variant of [`apply`](##state.EditorState.apply) that
// returns the precise transactions that were applied (which might
// be influenced by the [transaction
// hooks](##state.PluginSpec.filterTransaction) of
// plugins) along with the new state.
EditorState.prototype.applyTransaction = function applyTransaction (tr) {
    var this$1 = this;

  if (!this.filterTransaction(tr)) { return {state: this, transactions: []} }

  var trs = [tr], newState = this.applyInner(tr), seen = null
  // This loop repeatedly gives plugins a chance to respond to
  // transactions as new transactions are added, making sure to only
  // pass the transactions the plugin did not see before.
  outer: for (;;) {
    var haveNew = false
    for (var i = 0; i < this.config.plugins.length; i++) {
      var plugin = this$1.config.plugins[i]
      if (plugin.spec.appendTransaction) {
        var n = seen ? seen[i].n : 0, oldState = seen ? seen[i].state : this$1
        var tr$1 = n < trs.length &&
            plugin.spec.appendTransaction.call(plugin, n ? trs.slice(n) : trs, oldState, newState)
        if (tr$1 && newState.filterTransaction(tr$1, i)) {
          tr$1.setMeta("appendedTransaction", tr$1)
          if (!seen) {
            seen = []
            for (var j = 0; j < this.config.plugins.length; j++)
              { seen.push(j < i ? {state: newState, n: trs.length} : {state: this$1, n: 0}) }
          }
          trs.push(tr$1)
          newState = newState.applyInner(tr$1)
          haveNew = true
        }
        if (seen) { seen[i] = {state: newState, n: trs.length} }
      }
    }
    if (!haveNew) { return {state: newState, transactions: trs} }
  }
};

// : (Transaction) → EditorState
EditorState.prototype.applyInner = function applyInner (tr) {
    var this$1 = this;

  if (!tr.before.eq(this.doc)) { throw new RangeError("Applying a mismatched transaction") }
  var newInstance = new EditorState(this.config), fields = this.config.fields
  for (var i = 0; i < fields.length; i++) {
    var field = fields[i]
    newInstance[field.name] = field.apply(tr, this$1[field.name], this$1, newInstance)
  }
  for (var i$1 = 0; i$1 < applyListeners.length; i$1++) { applyListeners[i$1](this$1, tr, newInstance) }
  return newInstance
};

// :: Transaction
// Start a [transaction](#state.Transaction) from this state.
prototypeAccessors.tr.get = function () { return new Transaction(this) };

// :: (Object) → EditorState
// Create a state. `config` must be an object containing at least a
// `schema` (the schema to use) or `doc` (the starting document)
// property. When it has a `selection` property, that should be a
// valid [selection](#state.Selection) in the given document, to use
// as starting selection. Plugins, which are specified as an array
// in the `plugins` property, may read additional fields from the
// config object.
EditorState.create = function create (config) {
  var $config = new Configuration(config.schema || config.doc.type.schema, config.plugins)
  var instance = new EditorState($config)
  for (var i = 0; i < $config.fields.length; i++)
    { instance[$config.fields[i].name] = $config.fields[i].init(config, instance) }
  return instance
};

// :: (Object) → EditorState
// Create a new state based on this one, but with an adjusted set of
// active plugins. State fields that exist in both sets of plugins
// are kept unchanged. Those that no longer exist are dropped, and
// those that are new are initialized using their
// [`init`](#state.StateField.init) method, passing in the new
// configuration object..
EditorState.prototype.reconfigure = function reconfigure (config) {
    var this$1 = this;

  var $config = new Configuration(config.schema || this.schema, config.plugins)
  var fields = $config.fields, instance = new EditorState($config)
  for (var i = 0; i < fields.length; i++) {
    var name = fields[i].name
    instance[name] = this$1.hasOwnProperty(name) ? this$1[name] : fields[i].init(config, instance)
  }
  return instance
};

// :: (?Object<Plugin>) → Object
// Serialize this state to JSON. If you want to serialize the state
// of plugins, pass an object mapping property names to use in the
// resulting JSON object to plugin objects.
EditorState.prototype.toJSON = function toJSON (pluginFields) {
    var this$1 = this;

  var result = {doc: this.doc.toJSON(), selection: this.selection.toJSON()}
  if (pluginFields) { for (var prop in pluginFields) {
    if (prop == "doc" || prop == "selection")
      { throw new RangeError("The JSON fields `doc` and `selection` are reserved") }
    var plugin = pluginFields[prop], state = plugin.spec.state
    if (state && state.toJSON) { result[prop] = state.toJSON.call(plugin, this$1[plugin.key]) }
  } }
  return result
};

// :: (Object, Object, ?Object<Plugin>) → EditorState
// Deserialize a JSON representation of a state. `config` should
// have at least a `schema` field, and should contain array of
// plugins to initialize the state with. `pluginFields` can be used
// to deserialize the state of plugins, by associating plugin
// instances with the property names they use in the JSON object.
EditorState.fromJSON = function fromJSON (config, json, pluginFields) {
  if (!config.schema) { throw new RangeError("Required config field 'schema' missing") }
  var $config = new Configuration(config.schema, config.plugins)
  var instance = new EditorState($config)
  $config.fields.forEach(function (field) {
    if (field.name == "doc") {
      instance.doc = Node.fromJSON(config.schema, json.doc)
    } else if (field.name == "selection") {
      instance.selection = Selection.fromJSON(instance.doc, json.selection)
    } else {
      if (pluginFields) { for (var prop in pluginFields) {
        var plugin = pluginFields[prop], state = plugin.spec.state
        if (plugin.key == field.name && state && state.fromJSON &&
            Object.prototype.hasOwnProperty.call(json, prop)) {
          // This field belongs to a plugin mapped to a JSON field, read it from there.
          instance[field.name] = state.fromJSON.call(plugin, config, json[prop], instance)
          return
        }
      } }
      instance[field.name] = field.init(config, instance)
    }
  })
  return instance
};

// Kludge to allow the view to track mappings between different
// instances of a state.
EditorState.addApplyListener = function addApplyListener (f) {
  applyListeners.push(f)
};
EditorState.removeApplyListener = function removeApplyListener (f) {
  var found = applyListeners.indexOf(f)
  if (found > -1) { applyListeners.splice(found, 1) }
};

Object.defineProperties( EditorState.prototype, prototypeAccessors );
exports.EditorState = EditorState

var applyListeners = []


/***/ }),
/* 39 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var MarkType = ref.MarkType;
var Slice = ref.Slice;
var Fragment = ref.Fragment;

var ref$1 = __webpack_require__(9);
var Transform = ref$1.Transform;
var ref$2 = __webpack_require__(20);
var AddMarkStep = ref$2.AddMarkStep;
var RemoveMarkStep = ref$2.RemoveMarkStep;
var ref$3 = __webpack_require__(8);
var ReplaceStep = ref$3.ReplaceStep;

// :: (number, number, Mark) → Transform
// Add the given mark to the inline content between `from` and `to`.
Transform.prototype.addMark = function(from, to, mark) {
  var this$1 = this;

  var removed = [], added = [], removing = null, adding = null
  this.doc.nodesBetween(from, to, function (node, pos, parent, index) {
    if (!node.isInline) { return }
    var marks = node.marks
    if (mark.isInSet(marks) || !parent.contentMatchAt(index + 1).allowsMark(mark.type)) {
      adding = removing = null
    } else {
      var start = Math.max(pos, from), end = Math.min(pos + node.nodeSize, to)
      var rm = mark.type.isInSet(marks)

      if (!rm)
        { removing = null }
      else if (removing && removing.mark.eq(rm))
        { removing.to = end }
      else
        { removed.push(removing = new RemoveMarkStep(start, end, rm)) }

      if (adding)
        { adding.to = end }
      else
        { added.push(adding = new AddMarkStep(start, end, mark)) }
    }
  })

  removed.forEach(function (s) { return this$1.step(s); })
  added.forEach(function (s) { return this$1.step(s); })
  return this
}

// :: (number, number, ?union<Mark, MarkType>) → Transform
// Remove the given mark, or all marks of the given type, from inline
// nodes between `from` and `to`.
Transform.prototype.removeMark = function(from, to, mark) {
  var this$1 = this;
  if ( mark === void 0 ) mark = null;

  var matched = [], step = 0
  this.doc.nodesBetween(from, to, function (node, pos) {
    if (!node.isInline) { return }
    step++
    var toRemove = null
    if (mark instanceof MarkType) {
      var found = mark.isInSet(node.marks)
      if (found) { toRemove = [found] }
    } else if (mark) {
      if (mark.isInSet(node.marks)) { toRemove = [mark] }
    } else {
      toRemove = node.marks
    }
    if (toRemove && toRemove.length) {
      var end = Math.min(pos + node.nodeSize, to)
      for (var i = 0; i < toRemove.length; i++) {
        var style = toRemove[i], found$1 = (void 0)
        for (var j = 0; j < matched.length; j++) {
          var m = matched[j]
          if (m.step == step - 1 && style.eq(matched[j].style)) { found$1 = m }
        }
        if (found$1) {
          found$1.to = end
          found$1.step = step
        } else {
          matched.push({style: style, from: Math.max(pos, from), to: end, step: step})
        }
      }
    }
  })
  matched.forEach(function (m) { return this$1.step(new RemoveMarkStep(m.from, m.to, m.style)); })
  return this
}

// :: (number, number) → Transform
// Remove all marks and non-text inline nodes from the given range.
Transform.prototype.clearMarkup = function(from, to) {
  var this$1 = this;

  var delSteps = [] // Must be accumulated and applied in inverse order
  this.doc.nodesBetween(from, to, function (node, pos) {
    if (!node.isInline) { return }
    if (!node.type.isText) {
      delSteps.push(new ReplaceStep(pos, pos + node.nodeSize, Slice.empty))
      return
    }
    for (var i = 0; i < node.marks.length; i++)
      { this$1.step(new RemoveMarkStep(Math.max(pos, from), Math.min(pos + node.nodeSize, to), node.marks[i])) }
  })
  for (var i = delSteps.length - 1; i >= 0; i--) { this$1.step(delSteps[i]) }
  return this
}

Transform.prototype.clearNonMatching = function(pos, match) {
  var this$1 = this;

  var node = this.doc.nodeAt(pos)
  var delSteps = [], cur = pos + 1
  for (var i = 0; i < node.childCount; i++) {
    var child = node.child(i), end = cur + child.nodeSize
    var allowed = match.matchType(child.type, child.attrs)
    if (!allowed) {
      delSteps.push(new ReplaceStep(cur, end, Slice.empty))
    } else {
      match = allowed
      for (var j = 0; j < child.marks.length; j++) { if (!match.allowsMark(child.marks[j]))
        { this$1.step(new RemoveMarkStep(cur, end, child.marks[j])) } }
    }
    cur = end
  }
  if (!match.validEnd()) {
    var fill = match.fillBefore(Fragment.empty, true)
    this.replace(cur, cur, new Slice(fill, 0, 0))
  }
  for (var i$1 = delSteps.length - 1; i$1 >= 0; i$1--) { this$1.step(delSteps[i$1]) }
  return this
}


/***/ }),
/* 40 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Fragment = ref.Fragment;
var Slice = ref.Slice;

var ref$1 = __webpack_require__(8);
var ReplaceStep = ref$1.ReplaceStep;
var ReplaceAroundStep = ref$1.ReplaceAroundStep;
var ref$2 = __webpack_require__(9);
var Transform = ref$2.Transform;
var ref$3 = __webpack_require__(21);
var insertPoint = ref$3.insertPoint;

// :: (number, number, Slice) → Transform
// Replace a range of the document with a given slice, using `from`,
// `to`, and the slice's [`openLeft`](#model.Slice.openLeft) property
// as hints, rather than fixed start and end points. This method may
// grow the replaced area or close open nodes in the slice in order to
// get a fit that is more in line with WYSIWYG expectations, by
// dropping fully covered parent nodes of the replaced region when
// they are marked [non-defining](#model.NodeSpec.defining), or
// including an open parent node from the slice that _is_ marked as
// [defining](#model.NodeSpec.defining).
//
// This is the method, for example, to handle paste. The similar
// [`replace`](#transform.Transform.replace) method is a more
// primitive tool which will _not_ move the start and end of its given
// range, and is useful in situations where you need more precise
// control over what happens.
Transform.prototype.replaceRange = function(from, to, slice) {
  var this$1 = this;

  if (!slice.size) { return this.deleteRange(from, to) }

  var $from = this.doc.resolve(from), $to = this.doc.resolve(to)
  if (fitsTrivially($from, $to, slice))
    { return this.step(new ReplaceStep(from, to, slice)) }

  var canExpand = coveredDepths($from, this.doc.resolve(to)), preferredExpand = 0
  if (canExpand[canExpand.length - 1] == 0) { canExpand.pop() }
  canExpand.unshift($from.depth + 1)
  for (var d = $from.depth; d > 0; d--) {
    if ($from.node(d).type.spec.defining) { break }
    var found = canExpand.indexOf(d, 1)
    if (found > -1) { preferredExpand = found }
  }

  var leftNodes = [], preferredDepth = slice.openLeft
  for (var content = slice.content, i = 0;; i++) {
    var node = content.firstChild
    leftNodes.push(node)
    if (i == slice.openLeft) { break }
    content = node.content
  }
  // Back up if the node directly above openLeft, or the node above
  // that separated only by a non-defining textblock node, is defining.
  if (preferredDepth > 0 && leftNodes[preferredDepth - 1].type.spec.defining)
    { preferredDepth -= 1 }
  else if (preferredDepth >= 2 && leftNodes[preferredDepth - 1].isTextblock && leftNodes[preferredDepth - 2].type.spec.defining)
    { preferredDepth -= 2 }

  for (var j = slice.openLeft; j >= 0; j--) {
    var openDepth = (j + preferredDepth + 1) % (slice.openLeft + 1)
    var insert = leftNodes[openDepth]
    if (!insert) { continue }
    for (var i$1 = 0; i$1 < canExpand.length; i$1++) {
      // Loop over possible expansion levels, starting with the
      // preferred one
      var expandDepth = canExpand[(i$1 + preferredExpand) % canExpand.length]
      var parent = $from.node(expandDepth - 1), index = $from.index(expandDepth - 1)
      if (parent.canReplaceWith(index, index, insert.type, insert.attrs, insert.marks))
        { return this$1.replace($from.before(expandDepth), expandDepth > $from.depth ? to : $to.after(expandDepth),
                            new Slice(closeFragment(slice.content, 0, slice.openLeft, openDepth),
                                      openDepth, slice.openRight)) }
    }
  }

  return this.replace(from, to, slice)
}

function closeFragment(fragment, depth, oldOpen, newOpen, parent) {
  if (depth < oldOpen) {
    var first = fragment.firstChild
    fragment = fragment.replaceChild(0, first.copy(closeFragment(first.content, depth + 1, oldOpen, newOpen, first)))
  }
  if (depth > newOpen)
    { fragment = parent.contentMatchAt(0).fillBefore(fragment).append(fragment) }
  return fragment
}

// :: (number, number, Node) → Transform
// Replace the given range with a node, but use `from` and `to` as
// hints, rather than precise positions. When from and to are the same
// and are at the start or end of a parent node in which the given
// node doesn't fit, this method may _move_ them out towards a parent
// that does allow the given node to be placed. When the given range
// completely covers a parent node, this method may completely replace
// that parent node.
Transform.prototype.replaceRangeWith = function(from, to, node) {
  if (!node.isInline && from == to && this.doc.resolve(from).parent.content.size) {
    var point = insertPoint(this.doc, from, node.type, node.attrs)
    if (point != null) { from = to = point }
  }
  return this.replaceRange(from, to, new Slice(Fragment.from(node), 0, 0))
}

// :: (number, number) → Transform
// Delete the given range, expanding it to cover fully covered
// parent nodes until a valid replace is found.
Transform.prototype.deleteRange = function(from, to) {
  var $from = this.doc.resolve(from), $to = this.doc.resolve(to)
  var covered = coveredDepths($from, $to)
  for (var i = 0; i < covered.length; i++) {
    var depth = covered[i], last = i == covered.length - 1
    if ((last && depth == 0) || $from.node(depth).contentMatchAt(0).validEnd()) {
      from = $from.start(depth)
      to = $to.end(depth)
      break
    }
    if (depth > 0 && (last || $from.node(depth - 1).canReplace($from.index(depth - 1), $to.indexAfter(depth - 1)))) {
      from = $from.before(depth)
      to = $to.after(depth)
      break
    }
  }
  return this.delete(from, to)
}

// : (ResolvedPos, ResolvedPos) → [number]
// Returns an array of all depths for which $from - $to spans the
// whole content of the nodes at that depth.
function coveredDepths($from, $to) {
  var result = [], minDepth = Math.min($from.depth, $to.depth)
  for (var d = minDepth; d >= 0; d--) {
    var start = $from.start(d)
    if (start < $from.pos - ($from.depth - d) ||
        $to.end(d) > $to.pos + ($to.depth - d)) { break }
    if (start == $to.start(d)) { result.push(d) }
  }
  return result
}

// :: (number, number) → Transform
// Delete the content between the given positions.
Transform.prototype.delete = function(from, to) {
  return this.replace(from, to, Slice.empty)
}

// :: (Node, number, ?number, ?Slice) → ?Step
// "Fit" a slice into a given position in the document, producing a
// [step](#transform.Step) that inserts it.
function replaceStep(doc, from, to, slice) {
  if ( to === void 0 ) to = from;
  if ( slice === void 0 ) slice = Slice.empty;

  if (from == to && !slice.size) { return null }

  var $from = doc.resolve(from), $to = doc.resolve(to)
  // Optimization -- avoid work if it's obvious that it's not needed.
  if (fitsTrivially($from, $to, slice)) { return new ReplaceStep(from, to, slice) }
  var placed = placeSlice($from, slice)

  var fittedLeft = fitLeft($from, placed)
  var fitted = fitRight($from, $to, fittedLeft)
  if (!fitted) { return null }
  if (fittedLeft.size != fitted.size && canMoveText($from, $to, fittedLeft)) {
    var d = $to.depth, after = $to.after(d)
    while (d > 1 && after == $to.end(--d)) { ++after }
    var fittedAfter = fitRight($from, doc.resolve(after), fittedLeft)
    if (fittedAfter)
      { return new ReplaceAroundStep(from, after, to, $to.end(), fittedAfter, fittedLeft.size) }
  }
  return new ReplaceStep(from, to, fitted)
}
exports.replaceStep = replaceStep

// :: (number, ?number, ?Slice) → Transform
// Replace the part of the document between `from` and `to` with the
// given `slice`.
Transform.prototype.replace = function(from, to, slice) {
  if ( to === void 0 ) to = from;
  if ( slice === void 0 ) slice = Slice.empty;

  var step = replaceStep(this.doc, from, to, slice)
  if (step) { this.step(step) }
  return this
}

// :: (number, number, union<Fragment, Node, [Node]>) → Transform
// Replace the given range with the given content, which may be a
// fragment, node, or array of nodes.
Transform.prototype.replaceWith = function(from, to, content) {
  return this.replace(from, to, new Slice(Fragment.from(content), 0, 0))
}

// :: (number, union<Fragment, Node, [Node]>) → Transform
// Insert the given content at the given position.
Transform.prototype.insert = function(pos, content) {
  return this.replaceWith(pos, pos, content)
}



function fitLeftInner($from, depth, placed, placedBelow) {
  var content = Fragment.empty, openRight = 0, placedHere = placed[depth]
  if ($from.depth > depth) {
    var inner = fitLeftInner($from, depth + 1, placed, placedBelow || placedHere)
    openRight = inner.openRight + 1
    content = Fragment.from($from.node(depth + 1).copy(inner.content))
  }

  if (placedHere) {
    content = content.append(placedHere.content)
    openRight = placedHere.openRight
  }
  if (placedBelow) {
    content = content.append($from.node(depth).contentMatchAt($from.indexAfter(depth)).fillBefore(Fragment.empty, true))
    openRight = 0
  }

  return {content: content, openRight: openRight}
}

function fitLeft($from, placed) {
  var ref = fitLeftInner($from, 0, placed, false);
  var content = ref.content;
  var openRight = ref.openRight;
  return new Slice(content, $from.depth, openRight || 0)
}

function fitRightJoin(content, parent, $from, $to, depth, openLeft, openRight) {
  var match, count = content.childCount, matchCount = count - (openRight > 0 ? 1 : 0)
  if (openLeft < 0)
    { match = parent.contentMatchAt(matchCount) }
  else if (count == 1 && openRight > 0)
    { match = $from.node(depth).contentMatchAt(openLeft ? $from.index(depth) : $from.indexAfter(depth)) }
  else
    { match = $from.node(depth).contentMatchAt($from.indexAfter(depth))
      .matchFragment(content, count > 0 && openLeft ? 1 : 0, matchCount) }

  var toNode = $to.node(depth)
  if (openRight > 0 && depth < $to.depth) {
    var after = toNode.content.cutByIndex($to.indexAfter(depth)).addToStart(content.lastChild)
    var joinable$1 = match.fillBefore(after, true)
    // Can't insert content if there's a single node stretched across this gap
    if (joinable$1 && joinable$1.size && openLeft > 0 && count == 1) { joinable$1 = null }

    if (joinable$1) {
      var inner = fitRightJoin(content.lastChild.content, content.lastChild, $from, $to,
                               depth + 1, count == 1 ? openLeft - 1 : -1, openRight - 1)
      if (inner) {
        var last = content.lastChild.copy(inner)
        if (joinable$1.size)
          { return content.cutByIndex(0, count - 1).append(joinable$1).addToEnd(last) }
        else
          { return content.replaceChild(count - 1, last) }
      }
    }
  }
  if (openRight > 0)
    { match = match.matchNode(count == 1 && openLeft > 0 ? $from.node(depth + 1) : content.lastChild) }

  // If we're here, the next level can't be joined, so we see what
  // happens if we leave it open.
  var toIndex = $to.index(depth)
  if (toIndex == toNode.childCount && !toNode.type.compatibleContent(parent.type)) { return null }
  var joinable = match.fillBefore(toNode.content, true, toIndex)
  if (!joinable) { return null }

  if (openRight > 0) {
    var closed = fitRightClosed(content.lastChild, openRight - 1, $from, depth + 1,
                                count == 1 ? openLeft - 1 : -1)
    content = content.replaceChild(count - 1, closed)
  }
  content = content.append(joinable)
  if ($to.depth > depth)
    { content = content.addToEnd(fitRightSeparate($to, depth + 1)) }
  return content
}

function fitRightClosed(node, openRight, $from, depth, openLeft) {
  var match, content = node.content, count = content.childCount
  if (openLeft >= 0)
    { match = $from.node(depth).contentMatchAt($from.indexAfter(depth))
      .matchFragment(content, openLeft > 0 ? 1 : 0, count) }
  else
    { match = node.contentMatchAt(count) }

  if (openRight > 0) {
    var closed = fitRightClosed(content.lastChild, openRight - 1, $from, depth + 1,
                                count == 1 ? openLeft - 1 : -1)
    content = content.replaceChild(count - 1, closed)
  }

  return node.copy(content.append(match.fillBefore(Fragment.empty, true)))
}

function fitRightSeparate($to, depth) {
  var node = $to.node(depth)
  var fill = node.contentMatchAt(0).fillBefore(node.content, true, $to.index(depth))
  if ($to.depth > depth) { fill = fill.addToEnd(fitRightSeparate($to, depth + 1)) }
  return node.copy(fill)
}

function normalizeSlice(content, openLeft, openRight) {
  while (openLeft > 0 && openRight > 0 && content.childCount == 1) {
    content = content.firstChild.content
    openLeft--
    openRight--
  }
  return new Slice(content, openLeft, openRight)
}

// : (ResolvedPos, ResolvedPos, number, Slice) → Slice
function fitRight($from, $to, slice) {
  var fitted = fitRightJoin(slice.content, $from.node(0), $from, $to, 0, slice.openLeft, slice.openRight)
  if (!fitted) { return null }
  return normalizeSlice(fitted, slice.openLeft, $to.depth)
}

function fitsTrivially($from, $to, slice) {
  return !slice.openLeft && !slice.openRight && $from.start() == $to.start() &&
    $from.parent.canReplace($from.index(), $to.index(), slice.content)
}

function canMoveText($from, $to, slice) {
  if (!$to.parent.isTextblock) { return false }

  var match
  if (!slice.openRight) {
    var parent = $from.node($from.depth - (slice.openLeft - slice.openRight))
    if (!parent.isTextblock) { return false }
    match = parent.contentMatchAt(parent.childCount)
    if (slice.size)
      { match = match.matchFragment(slice.content, slice.openLeft ? 1 : 0) }
  } else {
    var parent$1 = nodeRight(slice.content, slice.openRight)
    if (!parent$1.isTextblock) { return false }
    match = parent$1.contentMatchAt(parent$1.childCount)
  }
  match = match.matchFragment($to.parent.content, $to.index())
  return match && match.validEnd()
}

// Algorithm for 'placing' the elements of a slice into a gap:
//
// We consider the content of each node that is open to the left to be
// independently placeable. I.e. in <p("foo"), p("bar")>, when the
// paragraph on the left is open, "foo" can be placed (somewhere on
// the left side of the replacement gap) independently from p("bar").
//
// So placeSlice splits up a slice into a number of sub-slices,
// along with information on where they can be placed on the given
// left-side edge. It works by walking the open side of the slice,
// from the inside out, and trying to find a landing spot for each
// element, by simultaneously scanning over the gap side. When no
// place is found for an open node's content, it is left in that node.
//
// If the outer content can't be placed, a set of wrapper nodes is
// made up for it (by rooting it in the document node type using
// findWrapping), and the algorithm continues to iterate over those.
// This is guaranteed to find a fit, since both stacks now start with
// the same node type (doc).

function nodeLeft(content, depth) {
  for (var i = 1; i < depth; i++) { content = content.firstChild.content }
  return content.firstChild
}

function nodeRight(content, depth) {
  for (var i = 1; i < depth; i++) { content = content.lastChild.content }
  return content.lastChild
}

// : (ResolvedPos, Slice) → [{content: Fragment, openRight: number, depth: number}]
function placeSlice($from, slice) {
  var dFrom = $from.depth, unplaced = null
  var placed = [], parents = null

  // Loop over the open side of the slice, trying to find a place for
  // each open fragment.
  for (var dSlice = slice.openLeft;; --dSlice) {
    // Get the components of the node at this level
    var curType = (void 0), curAttrs = (void 0), curFragment = (void 0)
    if (dSlice >= 0) {
      if (dSlice > 0) { // Inside slice
        ;var assign;
        ((assign = nodeLeft(slice.content, dSlice), curType = assign.type, curAttrs = assign.attrs, curFragment = assign.content, assign))
      } else if (dSlice == 0) { // Top of slice
        curFragment = slice.content
      }
      if (dSlice < slice.openLeft) { curFragment = curFragment.cut(curFragment.firstChild.nodeSize) }
    } else { // Outside slice, in generated wrappers (see below)
      curFragment = Fragment.empty
      var parent = parents[parents.length + dSlice - 1]
      curType = parent.type
      curAttrs = parent.attrs
    }
    // If the last iteration left unplaced content, include it in the fragment
    if (unplaced) { curFragment = curFragment.addToStart(unplaced) }

    // If there's nothing left to place, we're done
    if (curFragment.size == 0 && dSlice <= 0) { break }

    // This will go through the positions in $from, down from dFrom,
    // to find a fit
    var found = findPlacement(curFragment, $from, dFrom, placed)
    if (found && unneccesaryFallthrough($from, dFrom, found.depth, slice, dSlice))
      { found = null }

    if (found) {
      // If there was a fit, store it, and consider this content placed
      if (found.fragment.size > 0) { placed[found.depth] = {
        content: found.fragment,
        openRight: endOfContent(slice, dSlice) ? slice.openRight - dSlice : 0,
        depth: found.depth
      } }
      // If that was the last of the content, we're done
      if (dSlice <= 0) { break }
      unplaced = null
      dFrom = found.depth - (curType == $from.node(found.depth).type ? 1 : 0)
    } else {
      if (dSlice == 0) {
        // This is the top of the slice, and we haven't found a place to insert it.
        var top = $from.node(0)
        // Try to find a wrapping that makes its first child fit in the top node.
        var wrap = top.contentMatchAt($from.index(0)).findWrappingFor(curFragment.firstChild)
        // If no such thing exists, give up.
        if (!wrap || wrap.length == 0) { break }
        var last = wrap[wrap.length - 1]
        // Check that the fragment actually fits in the wrapping.
        if (!last.type.contentExpr.matches(last.attrs, curFragment)) { break }
        // Store the result for subsequent iterations.
        parents = [{type: top.type, attrs: top.attrs}].concat(wrap)
        ;var assign$1;
        ((assign$1 = last, curType = assign$1.type, curAttrs = assign$1.attrs, assign$1))
      }
      if (curFragment.size) {
        curFragment = curType.contentExpr.start(curAttrs).fillBefore(curFragment, true).append(curFragment)
        unplaced = curType.create(curAttrs, curFragment)
      } else {
        unplaced = null
      }
    }
  }

  return placed
}

function endOfContent(slice, depth) {
  for (var i = 0, content = slice.content; i < depth; i++) {
    if (content.childCount > 1) { return false }
    content = content.firstChild.content
  }
  return true
}

function findPlacement(fragment, $from, start, placed) {
  var hasMarks = false
  for (var i = 0; i < fragment.childCount; i++)
    { if (fragment.child(i).marks.length) { hasMarks = true } }
  for (var d = start; d >= 0; d--) {
    var startMatch = $from.node(d).contentMatchAt($from.indexAfter(d))
    var existing = placed[d]
    if (existing) { startMatch = startMatch.matchFragment(existing.content) }
    var match = startMatch.fillBefore(fragment)
    if (match) { return {depth: d, fragment: (existing ? existing.content.append(match) : match).append(fragment)} }
    if (hasMarks) {
      var stripped = matchStrippingMarks(startMatch, fragment)
      if (stripped) { return {depth: d, fragment: existing ? existing.content.append(stripped) : stripped} }
    }
  }
}

function matchStrippingMarks(match, fragment) {
  var newNodes = []
  for (var i = 0; i < fragment.childCount; i++) {
    var node = fragment.child(i), stripped = node.mark(node.marks.filter(function (m) { return match.allowsMark(m.type); }))
    match = match.matchNode(stripped)
    if (!match) { return null }
    newNodes.push(stripped)
  }
  return Fragment.from(newNodes)
}

function unneccesaryFallthrough($from, dFrom, dFound, slice, dSlice) {
  if (dSlice < 1) { return false }
  for (; dFrom > dFound; dFrom--) {
    var here = $from.node(dFrom).contentMatchAt($from.indexAfter(dFrom))
    for (var d = dSlice - 1; d >= 0; d--)
      { if (here.matchNode(nodeLeft(slice.content, d))) { return true } }
  }
  return false
}


/***/ }),
/* 41 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(1);
var Selection = ref.Selection;
var NodeSelection = ref.NodeSelection;
var TextSelection = ref.TextSelection;
var browser = __webpack_require__(5)
var ref$1 = __webpack_require__(6);
var domIndex = ref$1.domIndex;

function moveSelectionBlock(state, dir) {
  var ref = state.selection;
  var $anchor = ref.$anchor;
  var $head = ref.$head;
  var $side = dir > 0 ? $anchor.max($head) : $anchor.min($head)
  var $start = !$side.parent.inlineContent ? $side : $side.depth ? state.doc.resolve(dir > 0 ? $side.after() : $side.before()) : null
  return $start && Selection.findFrom($start, dir)
}

function apply(view, sel) {
  view.dispatch(view.state.tr.setSelection(sel).scrollIntoView())
  return true
}

function selectHorizontally(view, dir) {
  var sel = view.state.selection
  if (sel instanceof TextSelection) {
    if (!sel.empty) {
      return false
    } else if (view.endOfTextblock(dir > 0 ? "right" : "left")) {
      var next = moveSelectionBlock(view.state, dir)
      if (next && (next instanceof NodeSelection)) { return apply(view, next) }
      return false
    } else {
      var $head = sel.$head, node = $head.textOffset ? null : dir < 0 ? $head.nodeBefore : $head.nodeAfter
      if (node && NodeSelection.isSelectable(node))
        { return apply(view, new NodeSelection(dir < 0 ? view.state.doc.resolve($head.pos - node.nodeSize) : $head)) }
      return false
    }
  } else if (sel instanceof NodeSelection && sel.node.isInline) {
    return apply(view, new TextSelection(dir > 0 ? sel.$to : sel.$from))
  } else {
    var next$1 = moveSelectionBlock(view.state, dir)
    if (next$1) { return apply(view, next$1) }
    return false
  }
}

function nodeLen(node) {
  return node.nodeType == 3 ? node.nodeValue.length : node.childNodes.length
}

function isIgnorable(dom) {
  var desc = dom.pmViewDesc
  return desc && desc.size == 0
}

// Make sure the cursor isn't directly after one or more ignored
// nodes, which will confuse the browser's cursor motion logic.
function skipIgnoredNodesLeft(view) {
  var sel = view.root.getSelection()
  var node = sel.anchorNode, offset = sel.anchorOffset
  var moveNode, moveOffset
  for (;;) {
    if (offset > 0) {
      if (node.nodeType != 1) {
        if (node.nodeType == 3 && node.nodeValue.charAt(offset - 1) == "\ufeff") {
          moveNode = node
          moveOffset = --offset
        } else { break }
      } else {
        var before = node.childNodes[offset - 1]
        if (isIgnorable(before)) {
          moveNode = node
          moveOffset = --offset
        } else if (before.nodeType == 3) {
          node = before
          offset = node.nodeValue.length
        } else { break }
      }
    } else if (isBlockNode(node)) {
      break
    } else {
      var prev = node.previousSibling
      while (prev && isIgnorable(prev)) {
        moveNode = node.parentNode
        moveOffset = domIndex(prev)
        prev = prev.previousSibling
      }
      if (!prev) {
        node = node.parentNode
        if (node == view.dom) { break }
        offset = 0
      } else {
        node = prev
        offset = nodeLen(node)
      }
    }
  }
  if (moveNode) { setSel(sel, moveNode, moveOffset) }
}

// Make sure the cursor isn't directly before one or more ignored
// nodes.
function skipIgnoredNodesRight(view) {
  var sel = view.root.getSelection()
  var node = sel.anchorNode, offset = sel.anchorOffset, len = nodeLen(node)
  var moveNode, moveOffset
  for (;;) {
    if (offset < len) {
      if (node.nodeType != 1) { break }
      var after = node.childNodes[offset]
      if (isIgnorable(after)) {
        moveNode = node
        moveOffset = ++offset
      }
      else { break }
    } else if (isBlockNode(node)) {
      break
    } else {
      var next = node.nextSibling
      while (next && isIgnorable(next)) {
        moveNode = next.parentNode
        moveOffset = domIndex(next) + 1
        next = next.nextSibling
      }
      if (!next) {
        node = node.parentNode
        if (node == view.dom) { break }
        offset = len = 0
      } else {
        node = next
        offset = 0
        len = nodeLen(node)
      }
    }
  }
  if (moveNode) { setSel(sel, moveNode, moveOffset) }
}

function isBlockNode(dom) {
  var desc = dom.pmViewDesc
  return desc && desc.node && desc.node.isBlock
}

function setSel(sel, node, offset) {
  var range = document.createRange()
  range.setEnd(node, offset)
  range.setStart(node, offset)
  sel.removeAllRanges()
  sel.addRange(range)
}

// : (EditorState, number)
// Check whether vertical selection motion would involve node
// selections. If so, apply it (if not, the result is left to the
// browser)
function selectVertically(view, dir) {
  var sel = view.state.selection
  if (sel instanceof TextSelection && !sel.empty) { return false }
  var $from = sel.$from;
  var $to = sel.$to;

  if (!$from.parent.inlineContent || view.endOfTextblock(dir < 0 ? "up" : "down")) {
    var next = moveSelectionBlock(view.state, dir)
    if (next && (next instanceof NodeSelection))
      { return apply(view, next) }
  }
  if (!$from.parent.inlineContent) {
    var beyond = Selection.findFrom(dir < 0 ? $from : $to, dir)
    return beyond ? apply(view, beyond) : true
  }
  return false
}

function stopNativeHorizontalDelete(view, dir) {
  if (!(view.state.selection instanceof TextSelection)) { return true }
  var ref = view.state.selection;
  var $head = ref.$head;
  var $anchor = ref.$anchor;
  var empty = ref.empty;
  if (!$head.sameParent($anchor)) { return true }
  if (!empty) { return false }
  if (view.endOfTextblock(dir > 0 ? "forward" : "backward")) { return true }
  var nextNode = !$head.textOffset && (dir < 0 ? $head.nodeBefore : $head.nodeAfter)
  if (nextNode && !nextNode.isText) {
    var tr = view.state.tr
    if (dir < 0) { tr.delete($head.pos - nextNode.nodeSize, $head.pos) }
    else { tr.delete($head.pos, $head.pos + nextNode.nodeSize) }
    view.dispatch(tr)
    return true
  }
  return false
}

// A backdrop key mapping used to make sure we always suppress keys
// that have a dangerous default effect, even if the commands they are
// bound to return false, and to make sure that cursor-motion keys
// find a cursor (as opposed to a node selection) when pressed. For
// cursor-motion keys, the code in the handlers also takes care of
// block selections.

function getMods(event) {
  var result = ""
  if (event.ctrlKey) { result += "c" }
  if (event.metaKey) { result += "m" }
  if (event.altKey) { result += "a" }
  if (event.shiftKey) { result += "s" }
  return result
}

function captureKeyDown(view, event) {
  var code = event.keyCode, mods = getMods(event)
  if (code == 8) { // Backspace
    return stopNativeHorizontalDelete(view, -1) || skipIgnoredNodesLeft(view)
  } else if (code == 46) { // Delete
    return stopNativeHorizontalDelete(view, 1) || skipIgnoredNodesRight(view)
  } else if (code == 13 || code == 27) { // Enter, Esc
    return true
  } else if (code == 37) { // Left arrow
    return selectHorizontally(view, -1) || skipIgnoredNodesLeft(view)
  } else if (code == 39) { // Right arrow
    return selectHorizontally(view, 1) || skipIgnoredNodesRight(view)
  } else if (code == 38) { // Up arrow
    return selectVertically(view, -1)
  } else if (code == 40) { // Down arrow
    return selectVertically(view, 1)
  } else if (mods == (browser.mac ? "m" : "c") &&
             (code == 66 || code == 73 || code == 89 || code == 90)) { // Mod-[biyz]
    return true
  } else if (browser.mac && // Ctrl-[dh] and Alt-d on Mac
             ((code == 68 || code == 72) && mods == "c") ||
              (code == 68 && mods == "a")) {
    return stopNativeHorizontalDelete(view, code == 68 ? 1 : -1) || skipIgnoredNodesRight(view)
  }
  return false
}
exports.captureKeyDown = captureKeyDown


/***/ }),
/* 42 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var Slice = ref.Slice;
var Fragment = ref.Fragment;
var DOMParser = ref.DOMParser;
var DOMSerializer = ref.DOMSerializer;

function serializeForClipboard(view, slice) {
  var context = [];
  var content = slice.content;
  var openLeft = slice.openLeft;
  var openRight = slice.openRight;
  while (openLeft > 1 && openRight > 1 && content.childCount == 1 && content.firstChild.childCount == 1) {
    openLeft--
    openRight--
    var node = content.firstChild
    context.push(node.type.name, node.type.hasRequiredAttrs() ? node.attrs : null)
    content = node.content
  }

  var serializer = view.someProp("clipboardSerializer") || DOMSerializer.fromSchema(view.state.schema)
  var wrap = document.createElement("div")
  wrap.appendChild(serializer.serializeFragment(content))
  var child = wrap.firstChild.nodeType == 1 && wrap.firstChild
  if (child) {
    var singleNode = slice.openLeft == 0 && slice.openRight == 0 && slice.content.childCount == 1 && !slice.content.firstChild.isText
    child.setAttribute("data-pm-context", singleNode ? "none" : JSON.stringify(context))
  }
  return wrap
}
exports.serializeForClipboard = serializeForClipboard

// : (EditorView, string, string, ?bool, ResolvedPos) → ?Slice
// Read a slice of content from the clipboard (or drop data).
function parseFromClipboard(view, text, html, plainText, $context) {
  var dom, inCode = $context.parent.type.spec.code
  if (!html && !text) { return null }
  if ((plainText || inCode || !html) && text) {
    view.someProp("transformPastedText", function (f) { return text = f(text); })
    if (inCode) { return new Slice(Fragment.from(view.state.schema.text(text)), 0, 0) }
    dom = document.createElement("div")
    text.trim().split(/(?:\r\n?|\n)+/).forEach(function (block) {
      dom.appendChild(document.createElement("p")).textContent = block
    })
  } else {
    view.someProp("transformPastedHTML", function (f) { return html = f(html); })
    dom = readHTML(html)
  }

  var parser = view.someProp("clipboardParser") || view.someProp("domParser") || DOMParser.fromSchema(view.state.schema)
  var slice = parser.parseSlice(dom, {preserveWhitespace: true, context: $context})
  var contextNode = dom.querySelector("[data-pm-context]"), context = contextNode && contextNode.getAttribute("data-pm-context")
  if (context == "none")
    { slice = new Slice(slice.content, 0, 0) }
  else if (context)
    { slice = addContext(slice, context) }
  else // HTML wasn't created by ProseMirror. Make sure top-level siblings are coherent
    { slice = normalizeSiblings(slice, $context) }
  view.someProp("transformPasted", function (f) { slice = f(slice) })
  return slice
}
exports.parseFromClipboard = parseFromClipboard

// Takes a slice parsed with parseSlice, which means there hasn't been
// any content-expression checking done on the top nodes, tries to
// find a parent node in the current context that might fit the nodes,
// and if successful, rebuilds the slice so that it fits into that parent.
//
// This addresses the problem that Transform.replace expects a
// coherent slice, and will fail to place a set of siblings that don't
// fit anywhere in the schema.
function normalizeSiblings(slice, $context) {
  if (slice.content.childCount < 2) { return slice }
  var loop = function ( d ) {
    var parent = $context.node(d)
    var match = parent.contentMatchAt($context.index(d))
    var lastWrap = (void 0), result = []
    slice.content.forEach(function (node) {
      if (!result) { return }
      var wrap = match.findWrappingFor(node), inLast
      if (!wrap) { return result = null }
      if (inLast = result.length && lastWrap.length && addToSibling(wrap, lastWrap, node, result[result.length - 1], 0)) {
        result[result.length - 1] = inLast
      } else {
        if (result.length) { result[result.length - 1] = closeRight(result[result.length - 1], lastWrap.length) }
        var wrapped = withWrappers(node, wrap)
        result.push(wrapped)
        match = match.matchType(wrapped.type, wrapped.attrs)
        lastWrap = wrap
      }
    })
    if (result) { return { v: Slice.maxOpen(Fragment.from(result)) } }
  };

  for (var d = $context.depth; d >= 0; d--) {
    var returned = loop( d );

    if ( returned ) return returned.v;
  }
  return slice
}

function withWrappers(node, wrap, from) {
  if ( from === void 0 ) from = 0;

  for (var i = wrap.length - 1; i >= from; i--)
    { node = wrap[i].type.create(wrap[i].attrs, Fragment.from(node)) }
  return node
}

// Used to group adjacent nodes wrapped in similar parents by
// normalizeSiblings into the same parent node
function addToSibling(wrap, lastWrap, node, sibling, depth) {
  if (depth < wrap.length && depth < lastWrap.length && wrap[depth].type == lastWrap[depth].type) {
    var inner = addToSibling(wrap, lastWrap, node, sibling.lastChild, depth + 1)
    if (inner) { return sibling.copy(sibling.content.replaceChild(sibling.childCount - 1, inner)) }
    var match = sibling.contentMatchAt(sibling.childCount)
    if (depth == wrap.length - 1 ? match.matchNode(node) : match.matchType(wrap[depth + 1].type, wrap[depth + 1].attrs))
      { return sibling.copy(sibling.content.append(Fragment.from(withWrappers(node, wrap, depth + 1)))) }
  }
}

function closeRight(node, depth) {
  if (depth == 0) { return node }
  var fragment = node.content.replaceChild(node.childCount - 1, closeRight(node.lastChild, depth - 1))
  var fill = node.contentMatchAt(node.childCount).fillBefore(Fragment.empty, true)
  return node.copy(fragment.append(fill))
}

// Trick from jQuery -- some elements must be wrapped in other
// elements for innerHTML to work. I.e. if you do `div.innerHTML =
// "<td>..</td>"` the table cells are ignored.
var wrapMap = {thead: "table", colgroup: "table", col: "table colgroup",
                 tr: "table tbody", td: "table tbody tr", th: "table tbody tr"}
var detachedDoc = null
function readHTML(html) {
  var metas = /(\s*<meta [^>]*>)*/.exec(html)
  if (metas) { html = html.slice(metas[0].length) }
  var doc = detachedDoc || (detachedDoc = document.implementation.createHTMLDocument("title"))
  var elt = doc.createElement("div")
  var firstTag = /(?:<meta [^>]*>)*<([a-z][^>\s]+)/i.exec(html), wrap, depth = 0
  if (wrap = firstTag && wrapMap[firstTag[1].toLowerCase()]) {
    var nodes = wrap.split(" ")
    html = nodes.map(function (n) { return "<" + n + ">"; }).join("") + html + nodes.map(function (n) { return "</" + n + ">"; }).reverse().join("")
    depth = nodes.length
  }
  elt.innerHTML = html
  for (var i = 0; i < depth; i++) { elt = elt.firstChild }
  return elt
}

function addContext(slice, context) {
  if (!slice.size) { return slice }
  var schema = slice.content.firstChild.type.schema, array
  try { array = JSON.parse(context) }
  catch(e) { return slice }
  var content = slice.content;
  var openLeft = slice.openLeft;
  var openRight = slice.openRight;
  for (var i = array.length - 2; i >= 0; i -= 2) {
    var type = schema.nodes[array[i]]
    if (!type || type.hasRequiredAttrs()) { break }
    content = Fragment.from(type.create(array[i + 1], content))
    openLeft++; openRight++
  }
  return new Slice(content, openLeft, openRight)
}


/***/ }),
/* 43 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(6);
var textRange = ref.textRange;
var parentNode = ref.parentNode;

function windowRect() {
  return {left: 0, right: window.innerWidth,
          top: 0, bottom: window.innerHeight}
}

function scrollRectIntoView(view, rect) {
  var scrollThreshold = view.someProp("scrollThreshold") || 0, scrollMargin = view.someProp("scrollMargin")
  if (scrollMargin == null) { scrollMargin = 5 }
  for (var parent = view.dom;; parent = parentNode(parent)) {
    if (!parent) { break }
    var atBody = parent == document.body
    var bounding = atBody ? windowRect() : parent.getBoundingClientRect()
    var moveX = 0, moveY = 0
    if (rect.top < bounding.top + scrollThreshold)
      { moveY = -(bounding.top - rect.top + scrollMargin) }
    else if (rect.bottom > bounding.bottom - scrollThreshold)
      { moveY = rect.bottom - bounding.bottom + scrollMargin }
    if (rect.left < bounding.left + scrollThreshold)
      { moveX = -(bounding.left - rect.left + scrollMargin) }
    else if (rect.right > bounding.right - scrollThreshold)
      { moveX = rect.right - bounding.right + scrollMargin }
    if (moveX || moveY) {
      if (atBody) {
        window.scrollBy(moveX, moveY)
      } else {
        if (moveY) { parent.scrollTop += moveY }
        if (moveX) { parent.scrollLeft += moveX }
      }
    }
    if (atBody) { break }
  }
}
exports.scrollRectIntoView = scrollRectIntoView

// Store the scroll position of the editor's parent nodes, along with
// the top position of an element near the top of the editor, which
// will be used to make sure the visible viewport remains stable even
// when the size of the content above changes.
function storeScrollPos(view) {
  var rect = view.dom.getBoundingClientRect(), startY = Math.max(0, rect.top)
  var refDOM, refTop
  for (var x = (rect.left + rect.right) / 2, y = startY + 1;
       y < Math.min(innerHeight, rect.bottom); y += 5) {
    var dom = view.root.elementFromPoint(x, y)
    if (dom == view.dom || !view.dom.contains(dom)) { continue }
    var localRect = dom.getBoundingClientRect()
    if (localRect.top >= startY - 20) {
      refDOM = dom
      refTop = localRect.top
      break
    }
  }
  var stack = []
  for (var dom$1 = view.dom; dom$1; dom$1 = parentNode(dom$1)) {
    stack.push({dom: dom$1, top: dom$1.scrollTop, left: dom$1.scrollLeft})
    if (dom$1 == document.body) { break }
  }
  return {refDOM: refDOM, refTop: refTop, stack: stack}
}
exports.storeScrollPos = storeScrollPos

// Reset the scroll position of the editor's parent nodes to that what
// it was before, when storeScrollPos was called.
function resetScrollPos(ref) {
  var refDOM = ref.refDOM;
  var refTop = ref.refTop;
  var stack = ref.stack;

  var newRefTop = refDOM ? refDOM.getBoundingClientRect().top : 0
  var dTop = newRefTop == 0 ? 0 : newRefTop - refTop
  for (var i = 0; i < stack.length; i++) {
    var ref$1 = stack[i];
    var dom = ref$1.dom;
    var top = ref$1.top;
    var left = ref$1.left;
    if (dom.scrollTop != top + dTop) { dom.scrollTop = top + dTop }
    if (dom.scrollLeft != left) { dom.scrollLeft = left }
  }
}
exports.resetScrollPos = resetScrollPos

function findOffsetInNode(node, coords) {
  var closest, dxClosest = 2e8, coordsClosest, offset = 0
  var rowBot = coords.top, rowTop = coords.top
  for (var child = node.firstChild, childIndex = 0; child; child = child.nextSibling, childIndex++) {
    var rects = (void 0)
    if (child.nodeType == 1) { rects = child.getClientRects() }
    else if (child.nodeType == 3) { rects = textRange(child).getClientRects() }
    else { continue }

    for (var i = 0; i < rects.length; i++) {
      var rect = rects[i]
      if (rect.top <= rowBot && rect.bottom >= rowTop) {
        rowBot = Math.max(rect.bottom, rowBot)
        rowTop = Math.min(rect.top, rowTop)
        var dx = rect.left > coords.left ? rect.left - coords.left
            : rect.right < coords.left ? coords.left - rect.right : 0
        if (dx < dxClosest) {
          closest = child
          dxClosest = dx
          coordsClosest = dx && closest.nodeType == 3 ? {left: rect.right < coords.left ? rect.right : rect.left, top: coords.top} : coords
          if (child.nodeType == 1 && dx)
            { offset = childIndex + (coords.left >= (rect.left + rect.right) / 2 ? 1 : 0) }
          continue
        }
      }
      if (!closest && (coords.left >= rect.right && coords.top >= rect.top ||
                       coords.left >= rect.left && coords.top >= rect.bottom))
        { offset = childIndex + 1 }
    }
  }
  if (closest && closest.nodeType == 3) { return findOffsetInText(closest, coordsClosest) }
  if (!closest || (dxClosest && closest.nodeType == 1)) { return {node: node, offset: offset} }
  return findOffsetInNode(closest, coordsClosest)
}

function findOffsetInText(node, coords) {
  var len = node.nodeValue.length
  var range = document.createRange()
  for (var i = 0; i < len; i++) {
    range.setEnd(node, i + 1)
    range.setStart(node, i)
    var rect = singleRect(range, 1)
    if (rect.top == rect.bottom) { continue }
    if (rect.left - 1 <= coords.left && rect.right + 1 >= coords.left &&
        rect.top - 1 <= coords.top && rect.bottom + 1 >= coords.top)
      { return {node: node, offset: i + (coords.left >= (rect.left + rect.right) / 2 ? 1 : 0)} }
  }
  return {node: node, offset: 0}
}

function targetKludge(dom, coords) {
  if (/^[uo]l$/i.test(dom.nodeName)) {
    for (var child = dom.firstChild; child; child = child.nextSibling) {
      if (!child.pmViewDesc || !/^li$/i.test(child.nodeName)) { continue }
      var childBox = child.getBoundingClientRect()
      if (coords.left > childBox.left - 2) { break }
      if (childBox.top <= coords.top && childBox.bottom >= coords.top) { return child }
    }
  }
  return dom
}

function posFromElement(view, elt, coords) {
  elt = targetKludge(elt, coords)
  if (!view.dom.contains(elt.nodeType != 1 ? elt.parentNode : elt)) { return null }

  var ref = findOffsetInNode(elt, coords);
  var node = ref.node;
  var offset = ref.offset;
  var bias = -1
  if (node.nodeType == 1 && !node.firstChild) {
    var rect = node.getBoundingClientRect()
    bias = rect.left != rect.right && coords.left > (rect.left + rect.right) / 2 ? 1 : -1
  }
  return view.docView.posFromDOM(node, offset, bias)
}

function posFromCaret(view, node, offset, coords) {
  // Browser (in caretPosition/RangeFromPoint) will agressively
  // normalize towards nearby inline nodes. Since we are interested in
  // positions between block nodes too, we first walk up the hierarchy
  // of nodes to see if there are block nodes that the coordinates
  // fall outside of. If so, we take the position before/after that
  // block. If not, we call `posFromDOM` on the raw node/offset.
  var outside = -1
  for (var cur = node;;) {
    if (cur == view.dom) { break }
    var desc = view.docView.nearestDesc(cur, true)
    if (!desc) { return null }
    if (desc.node.isBlock) {
      var rect = desc.dom.getBoundingClientRect()
      if (rect.left > coords.left || rect.top > coords.top) { outside = desc.posBefore }
      else if (rect.right < coords.left || rect.bottom < coords.top) { outside = desc.posAfter }
      else { break }
    }
    cur = desc.dom.parentNode
  }
  return outside > -1 ? outside : view.docView.posFromDOM(node, offset)
}

// Given an x,y position on the editor, get the position in the document.
function posAtCoords(view, coords) {
  var root = view.root, node, offset
  if (root.caretPositionFromPoint) {
    var pos$1 = root.caretPositionFromPoint(coords.left, coords.top)
    if (pos$1) { var assign;
      ((assign = pos$1, node = assign.offsetNode, offset = assign.offset)) }
  }
  if (!node && root.caretRangeFromPoint) {
    var range = root.caretRangeFromPoint(coords.left, coords.top)
    if (range) { var assign$1;
      ((assign$1 = range, node = assign$1.startContainer, offset = assign$1.startOffset)) }
  }

  var elt = root.elementFromPoint(coords.left, coords.top + 1)
  if (!elt) { return null }
  var pos = node ? posFromCaret(view, node, offset, coords) : posFromElement(view, elt, coords)
  if (pos == null) { return null }

  var desc = view.docView.nearestDesc(elt, true)
  return {pos: pos, inside: desc ? desc.posAtStart - desc.border : -1}
}
exports.posAtCoords = posAtCoords

function singleRect(object, bias) {
  var rects = object.getClientRects()
  return !rects.length ? object.getBoundingClientRect() : rects[bias < 0 ? 0 : rects.length - 1]
}

// : (EditorView, number) → {left: number, top: number, right: number, bottom: number}
// Given a position in the document model, get a bounding box of the
// character at that position, relative to the window.
function coordsAtPos(view, pos) {
  var ref = view.docView.domFromPos(pos);
  var node = ref.node;
  var offset = ref.offset;
  var side, rect
  if (node.nodeType == 3) {
    if (offset < node.nodeValue.length) {
      rect = singleRect(textRange(node, offset, offset + 1), -1)
      side = "left"
    }
    if ((!rect || rect.left == rect.right) && offset) {
      rect = singleRect(textRange(node, offset - 1, offset), 1)
      side = "right"
    }
  } else if (node.firstChild) {
    if (offset < node.childNodes.length) {
      var child = node.childNodes[offset]
      rect = singleRect(child.nodeType == 3 ? textRange(child) : child, -1)
      side = "left"
    }
    if ((!rect || rect.top == rect.bottom) && offset) {
      var child$1 = node.childNodes[offset - 1]
      rect = singleRect(child$1.nodeType == 3 ? textRange(child$1) : child$1, 1)
      side = "right"
    }
  } else {
    rect = node.getBoundingClientRect()
    side = "left"
  }
  var x = rect[side]
  return {top: rect.top, bottom: rect.bottom, left: x, right: x}
}
exports.coordsAtPos = coordsAtPos

function withFlushedState(view, state, f) {
  var viewState = view.state, active = view.root.activeElement
  if (viewState != state || !view.inDOMChange) { view.updateState(state) }
  if (active != view.dom) { view.focus() }
  try {
    return f()
  } finally {
    if (viewState != state) { view.updateState(viewState) }
    if (active != view.dom) { active.focus() }
  }
}

// : (EditorView, number, number)
// Whether vertical position motion in a given direction
// from a position would leave a text block.
function endOfTextblockVertical(view, state, dir) {
  var sel = state.selection
  var $pos = dir == "up" ? sel.$anchor.min(sel.$head) : sel.$anchor.max(sel.$head)
  if (!$pos.depth) { return false }
  return withFlushedState(view, state, function () {
    var dom = view.docView.domAfterPos($pos.before())
    var coords = coordsAtPos(view, $pos.pos)
    for (var child = dom.firstChild; child; child = child.nextSibling) {
      var boxes = (void 0)
      if (child.nodeType == 1) { boxes = child.getClientRects() }
      else if (child.nodeType == 3) { boxes = textRange(child, 0, child.nodeValue.length).getClientRects() }
      else { continue }
      for (var i = 0; i < boxes.length; i++) {
        var box = boxes[i]
        if (dir == "up" ? box.bottom < coords.top + 1 : box.top > coords.bottom - 1)
          { return false }
      }
    }
    return true
  })
}

var maybeRTL = /[\u0590-\u08ac]/

function endOfTextblockHorizontal(view, state, dir) {
  var ref = state.selection;
  var $head = ref.$head;
  if (!$head.parent.isTextblock || !$head.depth) { return false }
  var offset = $head.parentOffset, atStart = !offset, atEnd = offset == $head.parent.content.size
  var sel = getSelection()
  // If the textblock is all LTR, or the browser doesn't support
  // Selection.modify (Edge), fall back to a primitive approach
  if (!maybeRTL.test($head.parent.textContent) || !sel.modify)
    { return dir == "left" || dir == "backward" ? atStart : atEnd }

  return withFlushedState(view, state, function () {
    // This is a huge hack, but appears to be the best we can
    // currently do: use `Selection.modify` to move the selection by
    // one character, and see if that moves the cursor out of the
    // textblock (or doesn't move it at all, when at the start/end of
    // the document).
    var oldRange = sel.getRangeAt(0), oldNode = sel.focusNode, oldOff = sel.focusOffset
    sel.modify("move", dir, "character")
    var parentDOM = view.docView.domAfterPos($head.before())
    var result = !parentDOM.contains(sel.focusNode.nodeType == 1 ? sel.focusNode : sel.focusNode.parentNode) ||
        (oldNode == sel.focusNode && oldOff == sel.focusOffset)
    // Restore the previous selection
    sel.removeAllRanges()
    sel.addRange(oldRange)
    return result
  })
}

var cachedState = null, cachedDir = null, cachedResult = false
function endOfTextblock(view, state, dir) {
  if (cachedState == state && cachedDir == dir) { return cachedResult }
  cachedState = state; cachedDir = dir
  return cachedResult = dir == "up" || dir == "down"
    ? endOfTextblockVertical(view, state, dir)
    : endOfTextblockHorizontal(view, state, dir)
}
exports.endOfTextblock = endOfTextblock


/***/ }),
/* 44 */
/***/ (function(module, exports, __webpack_require__) {

var browser = __webpack_require__(5)
var ref = __webpack_require__(23);
var DOMChange = ref.DOMChange;
var ref$1 = __webpack_require__(6);
var domIndex = ref$1.domIndex;

var observeOptions = {childList: true, characterData: true, attributes: true, subtree: true}
// IE11 has very broken mutation observers, so we also listen to DOMCharacterDataModified
var useCharData = browser.ie && browser.ie_version <= 11

var DOMObserver = function(view) {
  var this$1 = this;

  this.view = view
  this.observer = window.MutationObserver &&
    new window.MutationObserver(function (mutations) { return this$1.registerMutations(mutations); })
  if (useCharData)
    { this.onCharData = function (e) { return this$1.registerMutation({target: e.target, type: "characterData"}); } }
};

DOMObserver.prototype.start = function () {
  if (this.observer)
    { this.observer.observe(this.view.dom, observeOptions) }
  if (useCharData)
    { this.view.dom.addEventListener("DOMCharacterDataModified", this.onCharData) }
};

DOMObserver.prototype.stop = function () {
  if (this.observer) {
    this.flush()
    this.observer.disconnect()
  }
  if (useCharData)
    { this.view.dom.removeEventListener("DOMCharacterDataModified", this.onCharData) }
};

DOMObserver.prototype.flush = function () {
  if (this.observer)
    { this.registerMutations(this.observer.takeRecords()) }
};

DOMObserver.prototype.registerMutations = function (mutations) {
    var this$1 = this;

  for (var i = 0; i < mutations.length; i++)
    { this$1.registerMutation(mutations[i]) }
};

DOMObserver.prototype.registerMutation = function (mut) {
  if (!this.view.editable) { return }
  var desc = this.view.docView.nearestDesc(mut.target)
  if (mut.type == "attributes" &&
      (desc == this.view.docView || mut.attributeName == "contenteditable")) { return }
  if (!desc || desc.ignoreMutation(mut)) { return }

  var from, to
  if (mut.type == "childList") {
    var fromOffset = mut.previousSibling && mut.previousSibling.parentNode == mut.target
        ? domIndex(mut.previousSibling) + 1 : 0
    if (fromOffset == -1) { return }
    from = desc.localPosFromDOM(mut.target, fromOffset, -1)
    var toOffset = mut.nextSibling && mut.nextSibling.parentNode == mut.target
        ? domIndex(mut.nextSibling) : mut.target.childNodes.length
    if (toOffset == -1) { return }
    to = desc.localPosFromDOM(mut.target, toOffset, 1)
  } else if (mut.type == "attributes") {
    from = desc.posAtStart - desc.border
    to = desc.posAtEnd + desc.border
  } else { // "characterData"
    from = desc.posAtStart
    to = desc.posAtEnd
  }

  DOMChange.start(this.view).addRange(from, to)
};
exports.DOMObserver = DOMObserver


/***/ }),
/* 45 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(1);
var Selection = ref.Selection;
var NodeSelection = ref.NodeSelection;
var TextSelection = ref.TextSelection;

var browser = __webpack_require__(5)
var ref$1 = __webpack_require__(41);
var captureKeyDown = ref$1.captureKeyDown;
var ref$2 = __webpack_require__(23);
var DOMChange = ref$2.DOMChange;
var ref$3 = __webpack_require__(42);
var parseFromClipboard = ref$3.parseFromClipboard;
var serializeForClipboard = ref$3.serializeForClipboard;
var ref$4 = __webpack_require__(44);
var DOMObserver = ref$4.DOMObserver;
var ref$5 = __webpack_require__(13);
var selectionBetween = ref$5.selectionBetween;

// A collection of DOM events that occur within the editor, and callback functions
// to invoke when the event fires.
var handlers = {}, editHandlers = {}

function initInput(view) {
  view.shiftKey = false
  view.mouseDown = null
  view.dragging = null
  view.inDOMChange = null
  view.domObserver = new DOMObserver(view)
  view.domObserver.start()

  var loop = function ( event ) {
    var handler = handlers[event]
    view.dom.addEventListener(event, function (event) {
      if (eventBelongsToView(view, event) && !runCustomHandler(view, event) &&
          (view.editable || !(event.type in editHandlers)))
        { handler(view, event) }
    })
  };

  for (var event in handlers) loop( event );
  view.extraHandlers = Object.create(null)
  ensureListeners(view)
}
exports.initInput = initInput

function destroyInput(view) {
  view.domObserver.stop()
  if (view.inDOMChange) { view.inDOMChange.destroy() }
}
exports.destroyInput = destroyInput

function ensureListeners(view) {
  view.someProp("handleDOMEvents", function (currentHandlers) {
    for (var type in currentHandlers) { if (!view.extraHandlers[type] && !handlers.hasOwnProperty(type)) {
      view.extraHandlers[type] = true
      view.dom.addEventListener(type, function (event) { return runCustomHandler(view, event); })
    } }
  })
}
exports.ensureListeners = ensureListeners

function runCustomHandler(view, event) {
  return view.someProp("handleDOMEvents", function (handlers) {
    var handler = handlers[event.type]
    return handler ? handler(view, event) || event.defaultPrevented : false
  })
}

function eventBelongsToView(view, event) {
  if (!event.bubbles) { return true }
  if (event.defaultPrevented) { return false }
  for (var node = event.target; node != view.dom; node = node.parentNode)
    { if (!node || node.nodeType == 11 ||
        (node.pmViewDesc && node.pmViewDesc.stopEvent(event)))
      { return false } }
  return true
}

function dispatchEvent(view, event) {
  if (!runCustomHandler(view, event) && handlers[event.type] &&
      (view.editable || !(event.type in editHandlers)))
    { handlers[event.type](view, event) }
}
exports.dispatchEvent = dispatchEvent

editHandlers.keydown = function (view, event) {
  if (event.keyCode == 16) { view.shiftKey = true }
  if (view.inDOMChange) { return }
  if (view.someProp("handleKeyDown", function (f) { return f(view, event); }) || captureKeyDown(view, event))
    { event.preventDefault() }
  else
    { view.selectionReader.poll() }
}

editHandlers.keyup = function (view, e) {
  if (e.keyCode == 16) { view.shiftKey = false }
}

editHandlers.keypress = function (view, event) {
  if (view.inDOMChange || !event.charCode ||
      event.ctrlKey && !event.altKey || browser.mac && event.metaKey) { return }

  if (view.someProp("handleKeyPress", function (f) { return f(view, event); })) {
    event.preventDefault()
    return
  }

  var sel = view.state.selection
  if (!(sel instanceof TextSelection) || !sel.$from.sameParent(sel.$to)) {
    var text = String.fromCharCode(event.charCode)
    if (!view.someProp("handleTextInput", function (f) { return f(view, sel.$from.pos, sel.$to.pos, text); }))
      { view.dispatch(view.state.tr.insertText(text).scrollIntoView()) }
    event.preventDefault()
  }
}

function eventCoords(event) { return {left: event.clientX, top: event.clientY} }

var lastClick = {time: 0, x: 0, y: 0}, oneButLastClick = lastClick

function isNear(event, click) {
  var dx = click.x - event.clientX, dy = click.y - event.clientY
  return dx * dx + dy * dy < 100
}

function runHandlerOnContext(view, propName, pos, inside, event) {
  if (inside == -1) { return false }
  var $pos = view.state.doc.resolve(inside)
  var loop = function ( i ) {
    if (view.someProp(propName, function (f) { return i > $pos.depth ? f(view, pos, $pos.nodeAfter, $pos.before(i), event, true)
                                                    : f(view, pos, $pos.node(i), $pos.before(i), event, false); }))
      { return { v: true } }
  };

  for (var i = $pos.depth + 1; i > 0; i--) {
    var returned = loop( i );

    if ( returned ) return returned.v;
  }
  return false
}

function updateSelection(view, selection, origin) {
  if (!view.focused) { view.focus() }
  var tr = view.state.tr.setSelection(selection)
  if (origin == "pointer") { tr.setMeta("pointer", true) }
  view.dispatch(tr)
}

function selectClickedLeaf(view, inside) {
  if (inside == -1) { return false }
  var $pos = view.state.doc.resolve(inside), node = $pos.nodeAfter
  if (node && node.isAtom && NodeSelection.isSelectable(node)) {
    updateSelection(view, new NodeSelection($pos), "pointer")
    return true
  }
  return false
}

function selectClickedNode(view, inside) {
  if (inside == -1) { return false }
  var sel = view.state.selection, selectedNode, selectAt
  if (sel instanceof NodeSelection) { selectedNode = sel.node }

  var $pos = view.state.doc.resolve(inside)
  for (var i = $pos.depth + 1; i > 0; i--) {
    var node = i > $pos.depth ? $pos.nodeAfter : $pos.node(i)
    if (NodeSelection.isSelectable(node)) {
      if (selectedNode && sel.$from.depth > 0 &&
          i >= sel.$from.depth && $pos.before(sel.$from.depth + 1) == sel.$from.pos)
        { selectAt = $pos.before(sel.$from.depth) }
      else
        { selectAt = $pos.before(i) }
      break
    }
  }

  if (selectAt != null) {
    updateSelection(view, NodeSelection.create(view.state.doc, selectAt), "pointer")
    return true
  } else {
    return false
  }
}

function handleSingleClick(view, pos, inside, event, selectNode) {
  return runHandlerOnContext(view, "handleClickOn", pos, inside, event) ||
    view.someProp("handleClick", function (f) { return f(view, pos, event); }) ||
    (selectNode ? selectClickedNode(view, inside) : selectClickedLeaf(view, inside))
}

function handleDoubleClick(view, pos, inside, event) {
  return runHandlerOnContext(view, "handleDoubleClickOn", pos, inside, event) ||
    view.someProp("handleDoubleClick", function (f) { return f(view, pos, event); })
}

function handleTripleClick(view, pos, inside, event) {
  return runHandlerOnContext(view, "handleTripleClickOn", pos, inside, event) ||
    view.someProp("handleTripleClick", function (f) { return f(view, pos, event); }) ||
    defaultTripleClick(view, inside)
}

function defaultTripleClick(view, inside) {
  var doc = view.state.doc
  if (inside == -1) {
    if (doc.inlineContent) {
      updateSelection(view, TextSelection.create(doc, 0, doc.content.size), "pointer")
      return true
    }
    return false
  }

  var $pos = doc.resolve(inside)
  for (var i = $pos.depth + 1; i > 0; i--) {
    var node = i > $pos.depth ? $pos.nodeAfter : $pos.node(i)
    var nodePos = $pos.before(i)
    if (node.inlineContent)
      { updateSelection(view, TextSelection.create(doc, nodePos + 1, nodePos + 1 + node.content.size), "pointer") }
    else if (NodeSelection.isSelectable(node))
      { updateSelection(view, NodeSelection.create(doc, nodePos), "pointer") }
    else
      { continue }
    return true
  }
}

function forceDOMFlush(view) {
  if (!view.inDOMChange) { return false }
  view.inDOMChange.finish(true)
  return true
}

var selectNodeModifier = browser.mac ? "metaKey" : "ctrlKey"

handlers.mousedown = function (view, event) {
  var flushed = forceDOMFlush(view)
  var now = Date.now(), type
  if (now - lastClick.time >= 500 || !isNear(event, lastClick) || event[selectNodeModifier]) { type = "singleClick" }
  else if (now - oneButLastClick.time >= 600 || !isNear(event, oneButLastClick)) { type = "doubleClick" }
  else { type = "tripleClick" }
  oneButLastClick = lastClick
  lastClick = {time: now, x: event.clientX, y: event.clientY}

  var pos = view.posAtCoords(eventCoords(event))
  if (!pos) { return }

  if (type == "singleClick")
    { view.mouseDown = new MouseDown(view, pos, event, flushed) }
  else if ((type == "doubleClick" ? handleDoubleClick : handleTripleClick)(view, pos.pos, pos.inside, event))
    { event.preventDefault() }
  else
    { view.selectionReader.poll("pointer") }
}

var MouseDown = function(view, pos, event, flushed) {
  var this$1 = this;

  this.view = view
  this.pos = pos
  this.event = event
  this.flushed = flushed
  this.selectNode = event[selectNodeModifier]
  this.allowDefault = event.shiftKey

  var targetNode, targetPos
  if (pos.inside > -1) {
    targetNode = view.state.doc.nodeAt(pos.inside)
    targetPos = pos.inside
  } else {
    var $pos = view.state.doc.resolve(pos.pos)
    targetNode = $pos.parent
    targetPos = $pos.depth ? $pos.before() : 0
  }

  this.mightDrag = null
  if (targetNode.type.spec.draggable && targetNode.type.spec.selectable !== false ||
      view.state.selection instanceof NodeSelection && targetPos == view.state.selection.from)
    { this.mightDrag = {node: targetNode, pos: targetPos} }

  this.target = flushed ? null : event.target
  if (this.target && this.mightDrag) {
    this.view.domObserver.stop()
    this.target.draggable = true
    if (browser.gecko && (this.setContentEditable = !this.target.hasAttribute("contentEditable")))
      { setTimeout(function () { return this$1.target.setAttribute("contentEditable", "false"); }, 20) }
    this.view.domObserver.start()
  }

  view.root.addEventListener("mouseup", this.up = this.up.bind(this))
  view.root.addEventListener("mousemove", this.move = this.move.bind(this))
  view.selectionReader.poll("pointer")
};

MouseDown.prototype.done = function () {
  this.view.root.removeEventListener("mouseup", this.up)
  this.view.root.removeEventListener("mousemove", this.move)
  if (this.mightDrag && this.target) {
    this.view.domObserver.stop()
    this.target.draggable = false
    if (browser.gecko && this.setContentEditable)
      { this.target.removeAttribute("contentEditable") }
    this.view.domObserver.start()
  }
};

MouseDown.prototype.up = function (event) {
  this.done()

  if (!this.view.dom.contains(event.target.nodeType == 3 ? event.target.parentNode : event.target))
    { return }

  if (this.allowDefault) {
    this.view.selectionReader.poll("pointer")
  } else if (handleSingleClick(this.view, this.pos.pos, this.pos.inside, event, this.selectNode)) {
    event.preventDefault()
  } else if (this.flushed) {
    updateSelection(this.view, Selection.near(this.view.state.doc.resolve(this.pos.pos)), "pointer")
    event.preventDefault()
  } else {
    this.view.selectionReader.poll("pointer")
  }
};

MouseDown.prototype.move = function (event) {
  if (!this.allowDefault && (Math.abs(this.event.x - event.clientX) > 4 ||
                             Math.abs(this.event.y - event.clientY) > 4))
    { this.allowDefault = true }
  this.view.selectionReader.poll("pointer")
};

handlers.touchdown = function (view) {
  forceDOMFlush(view)
  view.selectionReader.poll("pointer")
}

handlers.contextmenu = function (view, e) {
  forceDOMFlush(view)
  var pos = view.posAtCoords(eventCoords(e))
  if (pos && view.someProp("handleContextMenu", function (f) { return f(view, pos.pos, e); }))
    { e.preventDefault() }
}

// Input compositions are hard. Mostly because the events fired by
// browsers are A) very unpredictable and inconsistent, and B) not
// cancelable.
//
// ProseMirror has the problem that it must not update the DOM during
// a composition, or the browser will cancel it. What it does is keep
// long-running operations (delayed DOM updates) when a composition is
// active.
//
// We _do not_ trust the information in the composition events which,
// apart from being very uninformative to begin with, is often just
// plain wrong. Instead, when a composition ends, we parse the dom
// around the original selection, and derive an update from that.

editHandlers.compositionstart = editHandlers.compositionupdate = function (view) {
  DOMChange.start(view, true)
}

editHandlers.compositionend = function (view, e) {
  if (!view.inDOMChange) {
    // We received a compositionend without having seen any previous
    // events for the composition. If there's data in the event
    // object, we assume that it's a real change, and start a
    // composition. Otherwise, we just ignore it.
    if (e.data) { DOMChange.start(view, true) }
    else { return }
  }

  view.inDOMChange.compositionEnd()
}

editHandlers.input = function (view) { return DOMChange.start(view); }

function captureCopy(view, dom) {
  // The extra wrapper is somehow necessary on IE/Edge to prevent the
  // content from being mangled when it is put onto the clipboard
  var wrap = document.body.appendChild(document.createElement("div"))
  wrap.appendChild(dom)
  wrap.style.cssText = "position: fixed; left: -10000px; top: 10px"
  var sel = getSelection(), range = document.createRange()
  range.selectNodeContents(dom)
  sel.removeAllRanges()
  sel.addRange(range)
  setTimeout(function () {
    document.body.removeChild(wrap)
    view.focus()
  }, 50)
}

// This is very crude, but unfortunately both these browsers _pretend_
// that they have a clipboard API—all the objects and methods are
// there, they just don't work, and they are hard to test.
// FIXME when Edge/Mobile Safari fixes this, change this to a version
// range test
var brokenClipboardAPI = browser.ie || browser.ios

handlers.copy = editHandlers.cut = function (view, e) {
  var sel = view.state.selection, cut = e.type == "cut"
  if (sel.empty) { return }

  // IE and Edge's clipboard interface is completely broken
  var data = brokenClipboardAPI ? null : e.clipboardData
  var slice = sel.content(), dom = serializeForClipboard(view, slice)
  if (data) {
    e.preventDefault()
    data.clearData()
    data.setData("text/html", dom.innerHTML)
    data.setData("text/plain", slice.content.textBetween(0, slice.content.size, "\n\n"))
  } else {
    captureCopy(view, dom)
  }
  if (cut) { view.dispatch(view.state.tr.deleteSelection().scrollIntoView()) }
}

function sliceSingleNode(slice) {
  return slice.openLeft == 0 && slice.openRight == 0 && slice.content.childCount == 1 ? slice.content.firstChild : null
}

function capturePaste(view, e) {
  var plainText = view.shiftKey || view.state.selection.$from.parent.type.spec.code
  var target = document.body.appendChild(document.createElement(plainText ? "textarea" : "div"))
  if (!plainText) { target.contentEditable = "true" }
  target.style.cssText = "position: fixed; left: -10000px; top: 10px"
  target.focus()
  setTimeout(function () {
    view.focus()
    document.body.removeChild(target)
    if (plainText) { doPaste(view, target.value, null, e) }
    else { doPaste(view, target.textContent, target.innerHTML, e) }
  }, 50)
}

function doPaste(view, text, html, e) {
  var slice = parseFromClipboard(view, text, html, view.shiftKey, view.state.selection.$from)
  if (!slice) { return false }

  if (view.someProp("handlePaste", function (f) { return f(view, e, slice); })) { return true }

  var singleNode = sliceSingleNode(slice)
  var tr = singleNode ? view.state.tr.replaceSelectionWith(singleNode) : view.state.tr.replaceSelection(slice)
  view.dispatch(tr.scrollIntoView())
  return true
}

editHandlers.paste = function (view, e) {
  var data = brokenClipboardAPI ? null : e.clipboardData
  if (data && doPaste(view, data.getData("text/plain"), data.getData("text/html"), e))
    { e.preventDefault() }
  else
    { capturePaste(view, e) }
}

var Dragging = function(slice, move) {
  this.slice = slice
  this.move = move
};

function dropPos(slice, $pos) {
  if (!slice || !slice.content.size) { return $pos.pos }
  var content = slice.content
  for (var i = 0; i < slice.openLeft; i++) { content = content.firstChild.content }
  for (var d = $pos.depth; d >= 0; d--) {
    var bias = d == $pos.depth ? 0 : $pos.pos <= ($pos.start(d + 1) + $pos.end(d + 1)) / 2 ? -1 : 1
    var insertPos = $pos.index(d) + (bias > 0 ? 1 : 0)
    if ($pos.node(d).canReplace(insertPos, insertPos, content))
      { return bias == 0 ? $pos.pos : bias < 0 ? $pos.before(d + 1) : $pos.after(d + 1) }
  }
  return $pos.pos
}

handlers.dragstart = function (view, e) {
  var mouseDown = view.mouseDown
  if (mouseDown) { mouseDown.done() }
  if (!e.dataTransfer) { return }

  var sel = view.state.selection
  var pos = sel.empty ? null : view.posAtCoords(eventCoords(e))
  if (pos && pos.pos >= sel.from && pos.pos <= sel.to) {
    // In selection
  } else if (mouseDown && mouseDown.mightDrag) {
    view.dispatch(view.state.tr.setSelection(NodeSelection.create(view.state.doc, mouseDown.mightDrag.pos)))
  } else {
    return
  }
  var slice = view.state.selection.content(), dom = serializeForClipboard(view, slice)
  e.dataTransfer.clearData()
  e.dataTransfer.setData("text/html", dom.innerHTML)
  e.dataTransfer.setData("text/plain", slice.content.textBetween(0, slice.content.size, "\n\n"))
  view.dragging = new Dragging(slice, !e.ctrlKey)
}

handlers.dragend = function (view) {
  window.setTimeout(function () { return view.dragging = null; }, 50)
}

editHandlers.dragover = editHandlers.dragenter = function (_, e) { return e.preventDefault(); }

editHandlers.drop = function (view, e) {
  var dragging = view.dragging
  view.dragging = null

  if (!e.dataTransfer) { return }

  var $mouse = view.state.doc.resolve(view.posAtCoords(eventCoords(e)).pos)
  if (!$mouse) { return }
  var slice = dragging && dragging.slice ||
      parseFromClipboard(view, e.dataTransfer.getData("text/plain"), e.dataTransfer.getData("text/html"), false, $mouse)
  if (!slice) { return }

  e.preventDefault()
  if (view.someProp("handleDrop", e, slice, dragging && dragging.move)) { return }
  var insertPos = dropPos(slice, view.state.doc.resolve($mouse.pos))

  var tr = view.state.tr
  if (dragging && dragging.move) { tr.deleteSelection() }

  var pos = tr.mapping.map(insertPos)
  var isNode = slice.openLeft == 0 && slice.openRight == 0 && slice.content.childCount == 1
  if (isNode)
    { tr.replaceRangeWith(pos, pos, slice.content.firstChild) }
  else
    { tr.replaceRange(pos, pos, slice) }
  var $pos = tr.doc.resolve(pos)
  if (isNode && NodeSelection.isSelectable(slice.content.firstChild) &&
      $pos.nodeAfter && $pos.nodeAfter.sameMarkup(slice.content.firstChild))
    { tr.setSelection(new NodeSelection($pos)) }
  else
    { tr.setSelection(selectionBetween(view, $pos, tr.doc.resolve(tr.mapping.map(insertPos)))) }
  view.focus()
  view.dispatch(tr)
}

handlers.focus = function (view, event) {
  if (!view.focused) {
    view.dom.classList.add("ProseMirror-focused")
    view.focused = true
  }
  view.someProp("onFocus", function (f) { f(view, event) })
}

handlers.blur = function (view, event) {
  if (view.focused) {
    view.dom.classList.remove("ProseMirror-focused")
    view.focused = false
  }
  view.someProp("onBlur", function (f) { f(view, event) })
}

// Make sure all handlers get registered
for (var prop in editHandlers) { handlers[prop] = editHandlers[prop] }


/***/ }),
/* 46 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(1);
var EditorState = ref.EditorState;
var ref$1 = __webpack_require__(2);
var Mapping = ref$1.Mapping;

var TrackedRecord = function(prev, mapping, state) {
  this.prev = prev
  this.mapping = mapping
  this.state = state
};

var TrackMappings = function(state) {
  this.seen = [new TrackedRecord(null, null, state)]
  // Kludge to listen to state changes globally in order to be able
  // to find mappings from a given state to another.
  EditorState.addApplyListener(this.track = this.track.bind(this))
};

TrackMappings.prototype.destroy = function () {
  EditorState.removeApplyListener(this.track)
};

TrackMappings.prototype.find = function (state) {
    var this$1 = this;

  for (var i = this.seen.length - 1; i >= 0; i--) {
    var record = this$1.seen[i]
    if (record.state == state) { return record }
  }
};

TrackMappings.prototype.track = function (old, tr, state) {
  var found = this.seen.length < 200 ? this.find(old) : null
  if (found)
    { this.seen.push(new TrackedRecord(found, tr.docChanged ? tr.mapping : null, state)) }
};

TrackMappings.prototype.getMapping = function (state, appendTo) {
  var found = this.find(state)
  if (!found) { return null }
  var mappings = []
  for (var rec = found; rec; rec = rec.prev)
    { if (rec.mapping) { mappings.push(rec.mapping) } }
  var result = appendTo || new Mapping
  for (var i = mappings.length - 1; i >= 0; i--)
    { result.appendMapping(mappings[i]) }
  return result
};
exports.TrackMappings = TrackMappings


/***/ }),
/* 47 */
/***/ (function(module, exports, __webpack_require__) {

var ref = __webpack_require__(0);
var DOMSerializer = ref.DOMSerializer;
var Fragment = ref.Fragment;

var ref$1 = __webpack_require__(6);
var domIndex = ref$1.domIndex;
var isEquivalentPosition = ref$1.isEquivalentPosition;
var browser = __webpack_require__(5)

// NodeView:: interface
//
// By default, document nodes are rendered using the result of the
// [`toDOM`](#view.NodeSpec.toDOM) method of their spec, and managed
// entirely by the editor. For some use cases, such as embedded
// node-specific editing interfaces, when you need more control over
// the behavior of a node's in-editor representation, and can
// [define](#view.EditorProps.nodeViews) a custom node view.
//
//   dom:: ?dom.Node
//   The outer DOM node that represents the document node. When not
//   given, the default strategy is used to create a DOM node.
//
//   contentDOM:: ?dom.Node
//   The DOM node that should hold the node's content. Only meaningful
//   if the node view also defines a `dom` property and if its node
//   type is not a leaf node type. When this is present, ProseMirror
//   will take care of rendering the node's children into it. When it
//   is not present, the node view itself is responsible for rendering
//   (or deciding not to render) its child nodes.
//
//   update:: ?(node: Node, decorations: [Decoration]) → bool
//   When given, this will be called when the view is updating itself.
//   It will be given a node (possibly of a different type), and an
//   array of active decorations (which are automatically drawn, and
//   the node view may ignore if it isn't interested in them), and
//   should return true if it was able to update to that node, and
//   false otherwise. If the node view has a `contentDOM` property (or
//   no `dom` property), updating its child nodes will be handled by
//   ProseMirror.
//
//   selectNode:: ?()
//   Can be used to override the way the node's selected status (as a
//   node selection) is displayed.
//
//   deselectNode:: ?()
//   When defining a `selectNode` method, you should also provide a
//   `deselectNode` method to disable it again.
//
//   setSelection:: ?(anchor: number, head: number, root: dom.Document)
//   This will be called to handle setting the selection inside the
//   node. By default, a DOM selection will be created between the DOM
//   positions corresponding to the given anchor and head positions,
//   but if you override it you can do something else.
//
//   stopEvent:: ?(event: dom.Event) → bool
//   Can be used to prevent the editor view from trying to handle some
//   or all DOM events that bubble up from the node view.
//
//   ignoreMutation:: ?(dom.MutationRecord) → bool
//   Called when a DOM
//   [mutation](https://developer.mozilla.org/en-US/docs/Web/API/MutationObserver)
//   happens within the view. Return false if the editor should
//   re-parse the range around the mutation, true if it can safely be
//   ignored.
//
//   destroy:: ?()
//   Called when the node view is removed from the editor or the whole
//   editor is detached.

// View descriptions are data structures that describe the DOM that is
// used to represent the editor's content. They are used for:
//
// - Incremental redrawing when the document changes
//
// - Figuring out what part of the document a given DOM position
//   corresponds to
//
// - Wiring in custom implementations of the editing interface for a
//   given node
//
// They form a doubly-linked mutable tree, starting at `view.docView`.

var NOT_DIRTY = 0, CHILD_DIRTY = 1, CONTENT_DIRTY = 2, NODE_DIRTY = 3

// Superclass for the various kinds of descriptions. Defines their
// basic structure and shared methods.
var ViewDesc = function(parent, children, dom, contentDOM) {
  this.parent = parent
  this.children = children
  this.dom = dom
  // An expando property on the DOM node provides a link back to its
  // description.
  dom.pmViewDesc = this
  // This is the node that holds the child views. It may be null for
  // descs that don't have children.
  this.contentDOM = contentDOM
  this.dirty = NOT_DIRTY
};

var prototypeAccessors = { size: {},border: {},posBefore: {},posAtStart: {},posAfter: {},posAtEnd: {},contentLost: {} };

// Used to check whether a given description corresponds to a
// widget/mark/node.
ViewDesc.prototype.matchesWidget = function () { return false };
ViewDesc.prototype.matchesMark = function () { return false };
ViewDesc.prototype.matchesNode = function () { return false };
ViewDesc.prototype.matchesHack = function () { return false };

// : () → ?ParseRule
// When parsing in-editor content (in domchange.js), we allow
// descriptions to determine the parse rules that should be used to
// parse them.
ViewDesc.prototype.parseRule = function () { return null };

// : (dom.Event) → bool
// Used by the editor's event handler to ignore events that come
// from certain descs.
ViewDesc.prototype.stopEvent = function () { return false };

// The size of the content represented by this desc.
prototypeAccessors.size.get = function () {
    var this$1 = this;

  var size = 0
  for (var i = 0; i < this.children.length; i++) { size += this$1.children[i].size }
  return size
};

// For block nodes, this represents the space taken up by their
// start/end tokens.
prototypeAccessors.border.get = function () { return 0 };

ViewDesc.prototype.destroy = function () {
    var this$1 = this;

  this.parent = null
  if (this.dom.pmViewDesc == this) { this.dom.pmViewDesc = null }
  for (var i = 0; i < this.children.length; i++)
    { this$1.children[i].destroy() }
};

ViewDesc.prototype.posBeforeChild = function (child) {
    var this$1 = this;

  for (var i = 0, pos = this.posAtStart; i < this.children.length; i++) {
    var cur = this$1.children[i]
    if (cur == child) { return pos }
    pos += cur.size
  }
};

prototypeAccessors.posBefore.get = function () {
  return this.parent.posBeforeChild(this)
};

prototypeAccessors.posAtStart.get = function () {
  return this.parent ? this.parent.posBeforeChild(this) + this.border : 0
};

prototypeAccessors.posAfter.get = function () {
  return this.posBefore + this.size
};

prototypeAccessors.posAtEnd.get = function () {
  return this.posAtStart + this.size - 2 * this.border
};

// : (dom.Node, number, ?number) → number
ViewDesc.prototype.localPosFromDOM = function (dom, offset, bias) {
    var this$1 = this;

  // If the DOM position is in the content, use the child desc after
  // it to figure out a position.
  if (this.contentDOM && this.contentDOM.contains(dom.nodeType == 1 ? dom : dom.parentNode)) {
    if (bias < 0) {
      var domBefore, desc
      if (dom == this.contentDOM) {
        domBefore = dom.childNodes[offset - 1]
      } else {
        while (dom.parentNode != this.contentDOM) { dom = dom.parentNode }
        domBefore = dom.previousSibling
      }
      while (domBefore && !((desc = domBefore.pmViewDesc) && desc.parent == this)) { domBefore = domBefore.previousSibling }
      return domBefore ? this.posBeforeChild(desc) + desc.size : this.posAtStart
    } else {
      var domAfter, desc$1
      if (dom == this.contentDOM) {
        domAfter = dom.childNodes[offset]
      } else {
        while (dom.parentNode != this.contentDOM) { dom = dom.parentNode }
        domAfter = dom.nextSibling
      }
      while (domAfter && !((desc$1 = domAfter.pmViewDesc) && desc$1.parent == this)) { domAfter = domAfter.nextSibling }
      return domAfter ? this.posBeforeChild(desc$1) : this.posAtEnd
    }
  }
  // Otherwise, use various heuristics, falling back on the bias
  // parameter, to determine whether to return the position at the
  // start or at the end of this view desc.
  var atEnd
  if (this.contentDOM && this.contentDOM != this.dom && this.dom.contains(this.contentDOM)) {
    atEnd = dom.compareDocumentPosition(this.contentDOM) & 2
  } else if (this.dom.firstChild) {
    if (offset == 0) { for (var search = dom;; search = search.parentNode) {
      if (search == this$1.dom) { atEnd = false; break }
      if (search.parentNode.firstChild != search) { break }
    } }
    if (atEnd == null && offset == dom.childNodes.length) { for (var search$1 = dom;; search$1 = search$1.parentNode) {
      if (search$1 == this$1.dom) { atEnd = true; break }
      if (search$1.parentNode.lastChild != search$1) { break }
    } }
  }
  return (atEnd == null ? bias > 0 : atEnd) ? this.posAtEnd : this.posAtStart
};

// Scan up the dom finding the first desc that is a descendant of
// this one.
ViewDesc.prototype.nearestDesc = function (dom, onlyNodes) {
    var this$1 = this;

  for (var first = true, cur = dom; cur; cur = cur.parentNode) {
    var desc = this$1.getDesc(cur)
    if (desc && (!onlyNodes || desc.node)) {
      // If dom is outside of this desc's nodeDOM, don't count it.
      if (first && desc.nodeDOM && !(desc.nodeDOM.nodeType == 1 ? desc.nodeDOM.contains(dom) : desc.nodeDOM == dom)) { first = false }
      else { return desc }
    }
  }
};

ViewDesc.prototype.getDesc = function (dom) {
    var this$1 = this;

  var desc = dom.pmViewDesc
  for (var cur = desc; cur; cur = cur.parent) { if (cur == this$1) { return desc } }
};

ViewDesc.prototype.posFromDOM = function (dom, offset, bias) {
    var this$1 = this;

  for (var scan = dom;; scan = scan.parentNode) {
    var desc = this$1.getDesc(scan)
    if (desc) { return desc.localPosFromDOM(dom, offset, bias) }
  }
};

// : (number) → ?NodeViewDesc
// Find the desc for the node after the given pos, if any. (When a
// parent node overrode rendering, there might not be one.)
ViewDesc.prototype.descAt = function (pos) {
    var this$1 = this;

  for (var i = 0, offset = 0; i < this.children.length; i++) {
    var child = this$1.children[i], end = offset + child.size
    if (offset == pos && end != offset) {
      while (!child.border && child.children.length) { child = child.children[0] }
      return child
    }
    if (pos < end) { return child.descAt(pos - offset - child.border) }
    offset = end
  }
};

// : (number) → {node: dom.Node, offset: number}
ViewDesc.prototype.domFromPos = function (pos) {
    var this$1 = this;

  if (!this.contentDOM) { return {node: this.dom, offset: 0} }
  for (var offset = 0, i = 0;; i++) {
    if (offset == pos)
      { return {node: this$1.contentDOM, offset: i} }
    if (i == this$1.children.length) { throw new Error("Invalid position " + pos) }
    var child = this$1.children[i], end = offset + child.size
    if (pos < end) { return child.domFromPos(pos - offset - child.border) }
    offset = end
  }
};

// Used to find a DOM range in a single parent for a given changed
// range.
ViewDesc.prototype.parseRange = function (from, to, base) {
    var this$1 = this;
    if ( base === void 0 ) base = 0;

  var fromOffset = -1, toOffset = -1
  for (var offset = 0, i = 0;; i++) {
    var child = this$1.children[i], end = offset + child.size
    if (fromOffset == -1 && from <= end) {
      var childBase = offset + child.border
      // FIXME maybe descend mark views to parse a narrower range?
      if (from >= childBase && to <= end - child.border && child.node &&
          child.contentDOM && this$1.contentDOM.contains(child.contentDOM))
        { return child.parseRange(from - childBase, to - childBase, base + childBase) }

      from = base + offset
      for (var j = i; j > 0; j--) {
        var prev = this$1.children[j - 1]
        if (prev.size && prev.dom.parentNode == this$1.contentDOM && !prev.emptyChildAt(1)) {
          fromOffset = domIndex(prev.dom) + 1
          break
        }
        from -= prev.size
      }
      if (fromOffset == -1) { fromOffset = 0 }
    }
    if (fromOffset > -1 && to <= end) {
      to = base + end
      for (var j$1 = i + 1; j$1 < this.children.length; j$1++) {
        var next = this$1.children[j$1]
        if (next.size && next.dom.parentNode == this$1.contentDOM && !next.emptyChildAt(-1)) {
          toOffset = domIndex(next.dom)
          break
        }
        to += next.size
      }
      if (toOffset == -1) { toOffset = this$1.contentDOM.childNodes.length }
      break
    }
    offset = end
  }
  return {node: this.contentDOM, from: from, to: to, fromOffset: fromOffset, toOffset: toOffset}
};

ViewDesc.prototype.emptyChildAt = function (side) {
  if (this.border || !this.contentDOM || !this.children.length) { return false }
  var child = this.children[side < 0 ? 0 : this.children.length - 1]
  return child.size == 0 || child.emptyChildAt(side)
};

// : (number) → dom.Node
ViewDesc.prototype.domAfterPos = function (pos) {
  var ref = this.domFromPos(pos);
    var node = ref.node;
    var offset = ref.offset;
  if (node.nodeType != 1 || offset == node.childNodes.length)
    { throw new RangeError("No node after pos " + pos) }
  return node.childNodes[offset]
};

// : (number, number, dom.Document)
// View descs are responsible for setting any selection that falls
// entirely inside of them, so that custom implementations can do
// custom things with the selection. Note that this falls apart when
// a selection starts in such a node and ends in another, in which
// case we just use whatever domFromPos produces as a best effort.
ViewDesc.prototype.setSelection = function (anchor, head, root) {
    var this$1 = this;

  // If the selection falls entirely in a child, give it to that child
  var from = Math.min(anchor, head), to = Math.max(anchor, head)
  for (var i = 0, offset = 0; i < this.children.length; i++) {
    var child = this$1.children[i], end = offset + child.size
    if (from > offset && to < end)
      { return child.setSelection(anchor - offset - child.border, head - offset - child.border, root) }
    offset = end
  }

  var anchorDOM = this.domFromPos(anchor), headDOM = this.domFromPos(head)
  var domSel = root.getSelection(), range = document.createRange()
  if (isEquivalentPosition(anchorDOM.node, anchorDOM.offset, domSel.anchorNode, domSel.anchorOffset) &&
      isEquivalentPosition(headDOM.node, headDOM.offset, domSel.focusNode, domSel.focusOffset))
    { return }

  // Selection.extend can be used to create an 'inverted' selection
  // (one where the focus is before the anchor), but not all
  // browsers support it yet.
  if (domSel.extend) {
    range.setEnd(anchorDOM.node, anchorDOM.offset)
    range.collapse(false)
  } else {
    if (anchor > head) { var tmp = anchorDOM; anchorDOM = headDOM; headDOM = tmp }
    range.setEnd(headDOM.node, headDOM.offset)
    range.setStart(anchorDOM.node, anchorDOM.offset)
  }
  domSel.removeAllRanges()
  domSel.addRange(range)
  if (domSel.extend)
    { domSel.extend(headDOM.node, headDOM.offset) }
};

// : (dom.MutationRecord) → bool
ViewDesc.prototype.ignoreMutation = function (_mutation) {
  return !this.contentDOM
};

prototypeAccessors.contentLost.get = function () {
  return this.contentDOM && this.contentDOM != this.dom && !this.dom.contains(this.contentDOM)
};

// Remove a subtree of the element tree that has been touched
// by a DOM change, so that the next update will redraw it.
ViewDesc.prototype.markDirty = function (from, to) {
    var this$1 = this;

  for (var offset = 0, i = 0; i < this.children.length; i++) {
    var child = this$1.children[i], end = offset + child.size
    if (offset == end ? from <= end && to >= offset : from < end && to > offset) {
      var startInside = offset + child.border, endInside = end - child.border
      if (from >= startInside && to <= endInside) {
        this$1.dirty = from == offset || to == end ? CONTENT_DIRTY : CHILD_DIRTY
        if (from == startInside && to == endInside && child.contentLost) { child.dirty = NODE_DIRTY }
        else { child.markDirty(from - startInside, to - startInside) }
        return
      } else {
        child.dirty = NODE_DIRTY
      }
    }
    offset = end
  }
  this.dirty = CONTENT_DIRTY
};

Object.defineProperties( ViewDesc.prototype, prototypeAccessors );

// Reused array to avoid allocating fresh arrays for things that will
// stay empty anyway.
var nothing = []

// A widget desc represents a widget decoration, which is a DOM node
// drawn between the document nodes.
var WidgetViewDesc = (function (ViewDesc) {
  function WidgetViewDesc(parent, widget) {
    ViewDesc.call(this, parent, nothing, widget.type.widget, null)
    this.widget = widget
  }

  if ( ViewDesc ) WidgetViewDesc.__proto__ = ViewDesc;
  WidgetViewDesc.prototype = Object.create( ViewDesc && ViewDesc.prototype );
  WidgetViewDesc.prototype.constructor = WidgetViewDesc;

  WidgetViewDesc.prototype.matchesWidget = function (widget) {
    return this.dirty == NOT_DIRTY && widget.type.eq(this.widget.type)
  };

  WidgetViewDesc.prototype.parseRule = function () { return {ignore: true} };

  WidgetViewDesc.prototype.stopEvent = function (event) {
    var stop = this.widget.spec.stopEvent
    return stop ? stop(event) : false
  };

  return WidgetViewDesc;
}(ViewDesc));

// A cursor wrapper is used to put the cursor in when newly typed text
// needs to be styled differently from its surrounding text (for
// example through storedMarks), so that the style of the text doesn't
// visually 'pop' between typing it and actually updating the view.
var CursorWrapperDesc = (function (WidgetViewDesc) {
  function CursorWrapperDesc () {
    WidgetViewDesc.apply(this, arguments);
  }

  if ( WidgetViewDesc ) CursorWrapperDesc.__proto__ = WidgetViewDesc;
  CursorWrapperDesc.prototype = Object.create( WidgetViewDesc && WidgetViewDesc.prototype );
  CursorWrapperDesc.prototype.constructor = CursorWrapperDesc;

  CursorWrapperDesc.prototype.parseRule = function () {
    var content
    for (var child = this.dom.firstChild; child; child = child.nextSibling) {
      var add = (void 0)
      if (child.nodeType == 3) {
        var text = child.nodeValue.replace(/\ufeff/g, "")
        if (!text) { continue }
        add = document.createTextNode(text)
      } else if (child.textContent == "\ufeff") {
        continue
      } else {
        add = child.cloneNode(true)
      }
      if (!content) { content = document.createDocumentFragment() }
      content.appendChild(add)
    }
    if (content) { return {skip: content} }
    else { return WidgetViewDesc.prototype.parseRule.call(this) }
  };

  CursorWrapperDesc.prototype.ignoreMutation = function () { return false };

  return CursorWrapperDesc;
}(WidgetViewDesc));

// A mark desc represents a mark. May have multiple children,
// depending on how the mark is split. Note that marks are drawn using
// a fixed nesting order, for simplicity and predictability, so in
// some cases they will be split more often than would appear
// necessary.
var MarkViewDesc = (function (ViewDesc) {
  function MarkViewDesc(parent, mark, dom) {
    ViewDesc.call(this, parent, [], dom, dom)
    this.mark = mark
  }

  if ( ViewDesc ) MarkViewDesc.__proto__ = ViewDesc;
  MarkViewDesc.prototype = Object.create( ViewDesc && ViewDesc.prototype );
  MarkViewDesc.prototype.constructor = MarkViewDesc;

  MarkViewDesc.create = function (parent, mark, view) {
    var custom = customNodeViews(view)[mark.type.name]
    var spec = custom && custom(mark, view)
    var dom = spec && spec.dom || DOMSerializer.renderSpec(document, mark.type.spec.toDOM(mark)).dom
    return new MarkViewDesc(parent, mark, dom)
  };

  MarkViewDesc.prototype.parseRule = function () { return {mark: this.mark.type.name, attrs: this.mark.attrs, contentElement: this.contentDOM} };

  MarkViewDesc.prototype.matchesMark = function (mark) { return this.dirty != NODE_DIRTY && this.mark.eq(mark) };

  MarkViewDesc.prototype.markDirty = function (from, to) {
    ViewDesc.prototype.markDirty.call(this, from, to)
    // Move dirty info to nearest node view
    if (this.dirty != NOT_DIRTY) {
      var parent = this.parent
      while (!parent.node) { parent = parent.parent }
      if (parent.dirty < this.dirty) { parent.dirty = this.dirty }
      this.dirty = NOT_DIRTY
    }
  };

  return MarkViewDesc;
}(ViewDesc));

// Node view descs are the main, most common type of view desc, and
// correspond to an actual node in the document. Unlike mark descs,
// they populate their child array themselves.
var NodeViewDesc = (function (ViewDesc) {
  function NodeViewDesc(parent, node, outerDeco, innerDeco, dom, contentDOM, nodeDOM, view) {
    ViewDesc.call(this, parent, node.isLeaf ? nothing : [], dom, contentDOM)
    this.nodeDOM = nodeDOM
    this.node = node
    this.outerDeco = outerDeco
    this.innerDeco = innerDeco
    if (contentDOM) { this.updateChildren(view) }
  }

  if ( ViewDesc ) NodeViewDesc.__proto__ = ViewDesc;
  NodeViewDesc.prototype = Object.create( ViewDesc && ViewDesc.prototype );
  NodeViewDesc.prototype.constructor = NodeViewDesc;

  var prototypeAccessors$1 = { size: {},border: {} };

  // By default, a node is rendered using the `toDOM` method from the
  // node type spec. But client code can use the `nodeViews` spec to
  // supply a custom node view, which can influence various aspects of
  // the way the node works.
  //
  // (Using subclassing for this was intentionally decided against,
  // since it'd require exposing a whole slew of finnicky
  // implementation details to the user code that they probably will
  // never need.)
  NodeViewDesc.create = function (parent, node, outerDeco, innerDeco, view) {
    var custom = customNodeViews(view)[node.type.name], descObj
    var spec = custom && custom(node, view, function () {
      // (This is a function that allows the custom view to find its
      // own position)
      if (descObj && descObj.parent) { return descObj.parent.posBeforeChild(descObj) }
    }, outerDeco)

    var dom = spec && spec.dom, contentDOM = spec && spec.contentDOM
    if (node.isText) {
      if (!dom) { dom = document.createTextNode(node.text) }
      else if (dom.nodeType != 3) { throw new RangeError("Text must be rendered as a DOM text node") }
    } else if (!dom) {
      ;var assign;
      ((assign = DOMSerializer.renderSpec(document, node.type.spec.toDOM(node)), dom = assign.dom, contentDOM = assign.contentDOM))
    }
    if (!contentDOM && !node.isText) { dom.contentEditable = false }

    var nodeDOM = dom
    dom = applyOuterDeco(dom, outerDeco, node)

    if (spec)
      { return descObj = new CustomNodeViewDesc(parent, node, outerDeco, innerDeco, dom, contentDOM, nodeDOM, spec, view) }
    else if (node.isText)
      { return new TextViewDesc(parent, node, outerDeco, innerDeco, dom, nodeDOM, view) }
    else
      { return new NodeViewDesc(parent, node, outerDeco, innerDeco, dom, contentDOM, nodeDOM, view) }
  };

  NodeViewDesc.prototype.parseRule = function () {
    var this$1 = this;

    // FIXME the assumption that this can always return the current
    // attrs means that if the user somehow manages to change the
    // attrs in the dom, that won't be picked up. Not entirely sure
    // whether this is a problem
    if (this.contentDOM && !this.contentLost)
      { return {node: this.node.type.name, attrs: this.node.attrs, contentElement: this.contentDOM} }
    else
      { return {node: this.node.type.name, attrs: this.node.attrs, getContent: function () { return this$1.contentDOM ? Fragment.empty : this$1.node.content; }} }
  };

  NodeViewDesc.prototype.matchesNode = function (node, outerDeco, innerDeco) {
    return this.dirty == NOT_DIRTY && node.eq(this.node) &&
      sameOuterDeco(outerDeco, this.outerDeco) && innerDeco.eq(this.innerDeco)
  };

  prototypeAccessors$1.size.get = function () { return this.node.nodeSize };

  prototypeAccessors$1.border.get = function () { return this.node.isLeaf ? 0 : 1 };

  // Syncs `this.children` to match `this.node.content` and the local
  // decorations, possibly introducing nesting for marks. Then, in a
  // separate step, syncs the DOM inside `this.contentDOM` to
  // `this.children`.
  NodeViewDesc.prototype.updateChildren = function (view) {
    var this$1 = this;

    var updater = new ViewTreeUpdater(this)
    iterDeco(this.node, this.innerDeco, function (widget) {
      if (widget.spec.isCursorWrapper)
        { updater.syncToMarks(widget.spec.marks, view) }
      // If the next node is a desc matching this widget, reuse it,
      // otherwise insert the widget as a new view desc.
      updater.placeWidget(widget)
    }, function (child, outerDeco, innerDeco, i) {
      // Make sure the wrapping mark descs match the node's marks.
      updater.syncToMarks(child.marks, view)
      // Either find an existing desc that exactly matches this node,
      // and drop the descs before it.
      updater.findNodeMatch(child, outerDeco, innerDeco) ||
        // Or try updating the next desc to reflect this node.
        updater.updateNextNode(child, outerDeco, innerDeco, view, this$1.node.content, i) ||
        // Or just add it as a new desc.
        updater.addNode(child, outerDeco, innerDeco, view)
    })
    // Drop all remaining descs after the current position.
    updater.syncToMarks(nothing, view)
    if (this.node.isTextblock) { updater.addTextblockHacks() }
    updater.destroyRest()

    // Sync the DOM if anything changed
    if (updater.changed || this.dirty == CONTENT_DIRTY) { this.renderChildren() }
  };

  NodeViewDesc.prototype.renderChildren = function () {
    renderDescs(this.contentDOM, this.children, NodeViewDesc.is)
    if (browser.ios) { iosHacks(this.dom) }
  };

  // : (Node, [Decoration], DecorationSet, EditorView) → bool
  // If this desc be updated to match the given node decoration,
  // do so and return true.
  NodeViewDesc.prototype.update = function (node, outerDeco, innerDeco, view) {
    if (this.dirty == NODE_DIRTY ||
        !node.sameMarkup(this.node)) { return false }
    this.updateInner(node, outerDeco, innerDeco, view)
    return true
  };

  NodeViewDesc.prototype.updateInner = function (node, outerDeco, innerDeco, view) {
    this.updateOuterDeco(outerDeco)
    this.node = node
    this.innerDeco = innerDeco
    if (this.contentDOM) { this.updateChildren(view) }
    this.dirty = NOT_DIRTY
  };

  NodeViewDesc.prototype.updateOuterDeco = function (outerDeco) {
    if (sameOuterDeco(outerDeco, this.outerDeco)) { return }
    var needsWrap = this.nodeDOM.nodeType != 1
    var oldDOM = this.dom
    this.dom = patchOuterDeco(this.dom, this.nodeDOM,
                              computeOuterDeco(this.outerDeco, this.node, needsWrap),
                              computeOuterDeco(outerDeco, this.node, needsWrap))
    if (this.dom != oldDOM) {
      oldDOM.pmViewDesc = null
      this.dom.pmViewDesc = this
    }
    this.outerDeco = outerDeco
  };

  // Mark this node as being the selected node.
  NodeViewDesc.prototype.selectNode = function () {
    this.nodeDOM.classList.add("ProseMirror-selectednode")
  };

  // Remove selected node marking from this node.
  NodeViewDesc.prototype.deselectNode = function () {
    this.nodeDOM.classList.remove("ProseMirror-selectednode")
  };

  Object.defineProperties( NodeViewDesc.prototype, prototypeAccessors$1 );

  return NodeViewDesc;
}(ViewDesc));

// Create a view desc for the top-level document node, to be exported
// and used by the view class.
function docViewDesc(doc, outerDeco, innerDeco, dom, view) {
  applyOuterDeco(dom, outerDeco, doc, true)
  return new NodeViewDesc(null, doc, outerDeco, innerDeco, dom, dom, dom, view)
}
exports.docViewDesc = docViewDesc

var TextViewDesc = (function (NodeViewDesc) {
  function TextViewDesc(parent, node, outerDeco, innerDeco, dom, nodeDOM, view) {
    NodeViewDesc.call(this, parent, node, outerDeco, innerDeco, dom, null, nodeDOM, view)
  }

  if ( NodeViewDesc ) TextViewDesc.__proto__ = NodeViewDesc;
  TextViewDesc.prototype = Object.create( NodeViewDesc && NodeViewDesc.prototype );
  TextViewDesc.prototype.constructor = TextViewDesc;

  TextViewDesc.prototype.parseRule = function () {
    return {skip: this.nodeDOM.parentNode}
  };

  TextViewDesc.prototype.update = function (node, outerDeco) {
    if (this.dirty == NODE_DIRTY || (this.dirty != NOT_DIRTY && !this.inParent()) ||
        !node.sameMarkup(this.node)) { return false }
    this.updateOuterDeco(outerDeco)
    if ((this.dirty != NOT_DIRTY || node.text != this.node.text) && node.text != this.nodeDOM.nodeValue)
      { this.nodeDOM.nodeValue = node.text }
    this.node = node
    this.dirty = NOT_DIRTY
    return true
  };

  TextViewDesc.prototype.inParent = function () {
    var parentDOM = this.parent.contentDOM
    for (var n = this.nodeDOM; n; n = n.parentNode) { if (n == parentDOM) { return true } }
    return false
  };

  TextViewDesc.prototype.domFromPos = function (pos) {
    return {node: this.nodeDOM, offset: pos}
  };

  TextViewDesc.prototype.localPosFromDOM = function (dom, offset, bias) {
    if (dom == this.nodeDOM) { return this.posAtStart + Math.min(offset, this.node.text.length) }
    return NodeViewDesc.prototype.localPosFromDOM.call(this, dom, offset, bias)
  };

  TextViewDesc.prototype.ignoreMutation = function (mutation) {
    return mutation.type != "characterData"
  };

  return TextViewDesc;
}(NodeViewDesc));

// A dummy desc used to tag trailing BR or span nodes created to work
// around contentEditable terribleness.
var BRHackViewDesc = (function (ViewDesc) {
  function BRHackViewDesc () {
    ViewDesc.apply(this, arguments);
  }

  if ( ViewDesc ) BRHackViewDesc.__proto__ = ViewDesc;
  BRHackViewDesc.prototype = Object.create( ViewDesc && ViewDesc.prototype );
  BRHackViewDesc.prototype.constructor = BRHackViewDesc;

  BRHackViewDesc.prototype.parseRule = function () { return {ignore: true} };
  BRHackViewDesc.prototype.matchesHack = function () { return this.dirty == NOT_DIRTY };

  return BRHackViewDesc;
}(ViewDesc));

// A separate subclass is used for customized node views, so that the
// extra checks only have to be made for nodes that are actually
// customized.
var CustomNodeViewDesc = (function (NodeViewDesc) {
  function CustomNodeViewDesc(parent, node, outerDeco, innerDeco, dom, contentDOM, nodeDOM, spec, view) {
    NodeViewDesc.call(this, parent, node, outerDeco, innerDeco, dom, contentDOM, nodeDOM, view)
    this.spec = spec
  }

  if ( NodeViewDesc ) CustomNodeViewDesc.__proto__ = NodeViewDesc;
  CustomNodeViewDesc.prototype = Object.create( NodeViewDesc && NodeViewDesc.prototype );
  CustomNodeViewDesc.prototype.constructor = CustomNodeViewDesc;

  // A custom `update` method gets to decide whether the update goes
  // through. If it does, and there's a `contentDOM` node, our logic
  // updates the children.
  CustomNodeViewDesc.prototype.update = function (node, outerDeco, innerDeco, view) {
    if (this.dirty == NODE_DIRTY) { return false }
    if (this.spec.update) {
      var result = this.spec.update(node, outerDeco)
      if (result) { this.updateInner(node, outerDeco, innerDeco, view) }
      return result
    } else if (!this.contentDOM && !node.isLeaf) {
      return false
    } else {
      return NodeViewDesc.prototype.update.call(this, node, outerDeco, this.contentDOM ? this.innerDeco : innerDeco, view)
    }
  };

  CustomNodeViewDesc.prototype.selectNode = function () {
    this.spec.selectNode ? this.spec.selectNode() : NodeViewDesc.prototype.selectNode.call(this)
  };

  CustomNodeViewDesc.prototype.deselectNode = function () {
    this.spec.deselectNode ? this.spec.deselectNode() : NodeViewDesc.prototype.deselectNode.call(this)
  };

  CustomNodeViewDesc.prototype.setSelection = function (anchor, head, root) {
    this.spec.setSelection ? this.spec.setSelection(anchor, head, root) : NodeViewDesc.prototype.setSelection.call(this, anchor, head, root)
  };

  CustomNodeViewDesc.prototype.destroy = function () {
    if (this.spec.destroy) { this.spec.destroy() }
    NodeViewDesc.prototype.destroy.call(this)
  };

  CustomNodeViewDesc.prototype.stopEvent = function (event) {
    return this.spec.stopEvent ? this.spec.stopEvent(event) : false
  };

  CustomNodeViewDesc.prototype.ignoreMutation = function (mutation) {
    return this.spec.ignoreMutation ? this.spec.ignoreMutation(mutation) : NodeViewDesc.prototype.ignoreMutation.call(this, mutation)
  };

  return CustomNodeViewDesc;
}(NodeViewDesc));

// : (dom.Node, [ViewDesc])
// Sync the content of the given DOM node with the nodes associated
// with the given array of view descs, recursing into mark descs
// because this should sync the subtree for a whole node at a time.
function renderDescs(parentDOM, descs) {
  var dom = parentDOM.firstChild
  for (var i = 0; i < descs.length; i++) {
    var desc = descs[i], childDOM = desc.dom
    if (childDOM.parentNode == parentDOM) {
      while (childDOM != dom) { dom = rm(dom) }
      dom = dom.nextSibling
    } else {
      parentDOM.insertBefore(childDOM, dom)
    }
    if (desc instanceof MarkViewDesc) {
      var pos = dom ? dom.previousSibling : parentDOM.lastChild
      renderDescs(desc.contentDOM, desc.children)
      dom = pos ? pos.nextSibling : parentDOM.firstChild
    }
  }
  while (dom) { dom = rm(dom) }
}

function OuterDecoLevel(nodeName) {
  if (nodeName) { this.nodeName = nodeName }
}
OuterDecoLevel.prototype = Object.create(null)

var noDeco = [new OuterDecoLevel]

function computeOuterDeco(outerDeco, node, needsWrap) {
  if (outerDeco.length == 0) { return noDeco }

  var top = needsWrap ? noDeco[0] : new OuterDecoLevel, result = [top]

  for (var i = 0; i < outerDeco.length; i++) {
    var attrs = outerDeco[i].type.attrs, cur = top
    if (!attrs) { continue }
    if (attrs.nodeName)
      { result.push(cur = new OuterDecoLevel(attrs.nodeName)) }

    for (var name in attrs) {
      var val = attrs[name]
      if (val == null) { continue }
      if (needsWrap && result.length == 1)
        { result.push(cur = top = new OuterDecoLevel(node.isInline ? "span" : "div")) }
      if (name == "class") { cur.class = (cur.class ? cur.class + " " : "") + val }
      else if (name == "style") { cur.style = (cur.style ? cur.style + ";" : "") + val }
      else if (name != "nodeName") { cur[name] = val }
    }
  }

  return result
}

function patchOuterDeco(outerDOM, nodeDOM, prevComputed, curComputed) {
  // Shortcut for trivial case
  if (prevComputed == noDeco && curComputed == noDeco) { return nodeDOM }

  var curDOM = nodeDOM
  for (var i = 0; i < curComputed.length; i++) {
    var deco = curComputed[i], prev = prevComputed[i]
    if (i) {
      var parent = (void 0)
      if (prev && prev.nodeName == deco.nodeName && curDOM != outerDOM &&
          (parent = nodeDOM.parentNode) && parent.tagName.toLowerCase() == deco.nodeName) {
        curDOM = parent
      } else {
        parent = document.createElement(deco.nodeName)
        parent.appendChild(curDOM)
        curDOM = parent
      }
    }
    patchAttributes(curDOM, prev || noDeco[0], deco)
  }
  return curDOM
}

function patchAttributes(dom, prev, cur) {
  for (var name in prev)
    { if (name != "class" && name != "style" && name != "nodeName" && !(name in cur))
      { dom.removeAttribute(name) } }
  for (var name$1 in cur)
    { if (name$1 != "class" && name$1 != "style" && name$1 != "nodeName" && cur[name$1] != prev[name$1])
      { dom.setAttribute(name$1, cur[name$1]) } }
  if (prev.class != cur.class) {
    var prevList = prev.class ? prev.class.split(" ") : nothing
    var curList = cur.class ? cur.class.split(" ") : nothing
    for (var i = 0; i < prevList.length; i++) { if (curList.indexOf(prevList[i]) == -1)
      { dom.classList.remove(prevList[i]) } }
    for (var i$1 = 0; i$1 < curList.length; i$1++) { if (prevList.indexOf(curList[i$1]) == -1)
      { dom.classList.add(curList[i$1]) } }
  }
  if (prev.style != cur.style) {
    var text = dom.style.cssText, found
    if (prev.style && (found = text.indexOf(prev.style)) > -1)
      { text = text.slice(0, found) + text.slice(found + prev.style.length) }
    dom.style.cssText = text + (cur.style || "")
  }
}

function applyOuterDeco(dom, deco, node) {
  return patchOuterDeco(dom, dom, noDeco, computeOuterDeco(deco, node, dom.nodeType != 1))
}

// : ([Decoration], [Decoration]) → bool
function sameOuterDeco(a, b) {
  if (a.length != b.length) { return false }
  for (var i = 0; i < a.length; i++) { if (!a[i].type.eq(b[i].type)) { return false } }
  return true
}

// Remove a DOM node and return its next sibling.
function rm(dom) {
  var next = dom.nextSibling
  dom.parentNode.removeChild(dom)
  return next
}

// Helper class for incrementally updating a tree of mark descs and
// the widget and node descs inside of them.
var ViewTreeUpdater = function(top) {
  this.top = top
  // Index into `this.top`'s child array, represents the current
  // update position.
  this.index = 0
  // When entering a mark, the current top and index are pushed
  // onto this.
  this.stack = []
  // Tracks whether anything was changed
  this.changed = false
};

// Destroy and remove the children between the given indices in
// `this.top`.
ViewTreeUpdater.prototype.destroyBetween = function (start, end) {
    var this$1 = this;

  if (start == end) { return }
  for (var i = start; i < end; i++) { this$1.top.children[i].destroy() }
  this.top.children.splice(start, end - start)
  this.changed = true
};

// Destroy all remaining children in `this.top`.
ViewTreeUpdater.prototype.destroyRest = function () {
  this.destroyBetween(this.index, this.top.children.length)
};

// : ([Mark], EditorView)
// Sync the current stack of mark descs with the given array of
// marks, reusing existing mark descs when possible.
ViewTreeUpdater.prototype.syncToMarks = function (marks, view) {
    var this$1 = this;

  var keep = 0, depth = this.stack.length >> 1
  var maxKeep = Math.min(depth, marks.length), next
  while (keep < maxKeep &&
         (keep == depth - 1 ? this.top : this.stack[(keep + 1) << 1]).matchesMark(marks[keep]))
    { keep++ }

  while (keep < depth) {
    this$1.destroyRest()
    this$1.top.dirty = NOT_DIRTY
    this$1.index = this$1.stack.pop()
    this$1.top = this$1.stack.pop()
    depth--
  }
  while (depth < marks.length) {
    this$1.stack.push(this$1.top, this$1.index + 1)
    if (this$1.index < this$1.top.children.length &&
        (next = this$1.top.children[this$1.index]).matchesMark(marks[depth])) {
      this$1.top = next
    } else {
      var markDesc = MarkViewDesc.create(this$1.top, marks[depth], view)
      this$1.top.children.splice(this$1.index, 0, markDesc)
      this$1.top = markDesc
      this$1.changed = true
    }
    this$1.index = 0
    depth++
  }
};

// : (Node, [Decoration], DecorationSet) → bool
// Try to find a node desc matching the given data. Skip over it and
// return true when successful.
ViewTreeUpdater.prototype.findNodeMatch = function (node, outerDeco, innerDeco) {
    var this$1 = this;

  for (var i = this.index, children = this.top.children, e = Math.min(children.length, i + 5); i < e; i++) {
    if (children[i].matchesNode(node, outerDeco, innerDeco)) {
      this$1.destroyBetween(this$1.index, i)
      this$1.index++
      return true
    }
  }
  return false
};

// : (Node, [Decoration], DecorationSet, EditorView, Fragment, number) → bool
// Try to update the next node, if any, to the given data. First
// tries scanning ahead in the siblings fragment to see if the next
// node matches any of those, and if so, doesn't touch it, to avoid
// overwriting nodes that could still be used.
ViewTreeUpdater.prototype.updateNextNode = function (node, outerDeco, innerDeco, view, siblings, index) {
  if (this.index == this.top.children.length) { return false }
  var next = this.top.children[this.index]
  if (next instanceof NodeViewDesc) {
    for (var i = index + 1, e = Math.min(siblings.childCount, i + 5); i < e; i++)
      { if (next.node == siblings.child(i)) { return false } }
    var nextDOM = next.dom
    if (next.update(node, outerDeco, innerDeco, view)) {
      if (next.dom != nextDOM) { this.changed = true }
      this.index++
      return true
    }
  }
  return false
};

// : (Node, [Decoration], DecorationSet, EditorView)
// Insert the node as a newly created node desc.
ViewTreeUpdater.prototype.addNode = function (node, outerDeco, innerDeco, view) {
  this.top.children.splice(this.index++, 0, NodeViewDesc.create(this.top, node, outerDeco, innerDeco, view))
  this.changed = true
};

ViewTreeUpdater.prototype.placeWidget = function (widget) {
  if (this.index < this.top.children.length && this.top.children[this.index].matchesWidget(widget)) {
    this.index++
  } else {
    var desc = new (widget.spec.isCursorWrapper ? CursorWrapperDesc : WidgetViewDesc)(this.top, widget)
    this.top.children.splice(this.index++, 0, desc)
    this.changed = true
  }
};

// Make sure a textblock looks and behaves correctly in
// contentEditable.
ViewTreeUpdater.prototype.addTextblockHacks = function () {
  var lastChild = this.top.children[this.index - 1]
  while (lastChild instanceof MarkViewDesc) { lastChild = lastChild.children[lastChild.children.length - 1] }

  if (!lastChild || // Empty textblock
      !(lastChild instanceof TextViewDesc) ||
      /\n$/.test(lastChild.node.text)) {
    if (this.index < this.top.children.length && this.top.children[this.index].matchesHack()) {
      this.index++
    } else {
      var dom = document.createElement("br")
      this.top.children.splice(this.index++, 0, new BRHackViewDesc(this.top, nothing, dom, null))
      this.changed = true
    }
  }
};

// : (ViewDesc, DecorationSet, (Decoration), (Node, [Decoration], DecorationSet))
// This function abstracts iterating over the nodes and decorations in
// a fragment. Calls `onNode` for each node, with its local and child
// decorations. Splits text nodes when there is a decoration starting
// or ending inside of them. Calls `onWidget` for each widget.
function iterDeco(parent, deco, onWidget, onNode) {
  var locals = deco.locals(parent), offset = 0
  // Simple, cheap variant for when there are no local decorations
  if (locals.length == 0) {
    for (var i = 0; i < parent.childCount; i++) {
      var child = parent.child(i)
      onNode(child, locals, deco.forChild(offset, child), i)
      offset += child.nodeSize
    }
    return
  }

  var decoIndex = 0, active = [], restNode = null
  for (var parentIndex = 0;;) {
    while (decoIndex < locals.length && locals[decoIndex].to == offset)
      { onWidget(locals[decoIndex++]) }

    var child$1 = (void 0)
    if (restNode) {
      child$1 = restNode
      restNode = null
    } else if (parentIndex < parent.childCount) {
      child$1 = parent.child(parentIndex++)
    } else {
      break
    }

    for (var i$1 = 0; i$1 < active.length; i$1++) { if (active[i$1].to <= offset) { active.splice(i$1--, 1) } }
    while (decoIndex < locals.length && locals[decoIndex].from == offset) { active.push(locals[decoIndex++]) }

    var end = offset + child$1.nodeSize
    if (child$1.isText) {
      var cutAt = end
      if (decoIndex < locals.length && locals[decoIndex].from < cutAt) { cutAt = locals[decoIndex].from }
      for (var i$2 = 0; i$2 < active.length; i$2++) { if (active[i$2].to < cutAt) { cutAt = active[i$2].to } }
      if (cutAt < end) {
        restNode = child$1.cut(cutAt - offset)
        child$1 = child$1.cut(0, cutAt - offset)
        end = cutAt
      }
    }

    onNode(child$1, active.length ? active.slice() : nothing, deco.forChild(offset, child$1), parentIndex - 1)
    offset = end
  }
}

// Pre-calculate and cache the set of custom view specs for a given
// prop object.
var cachedCustomViews, cachedCustomFor
function customNodeViews(view) {
  if (cachedCustomFor == view._props) { return cachedCustomViews }
  cachedCustomFor = view._props
  return cachedCustomViews = buildCustomViews(view)
}
function buildCustomViews(view) {
  var result = {}
  view.someProp("nodeViews", function (obj) {
    for (var prop in obj) { if (!Object.prototype.hasOwnProperty.call(result, prop))
      { result[prop] = obj[prop] } }
  })
  return result
}

// List markers in Mobile Safari will mysteriously disappear
// sometimes. This works around that.
function iosHacks(dom) {
  if (dom.nodeName == "UL" || dom.nodeName == "OL") {
    var oldCSS = dom.style.cssText
    dom.style.cssText = oldCSS + "; list-style: square !important"
    window.getComputedStyle(dom).listStyle
    dom.style.cssText = oldCSS
  }
}


/***/ }),
/* 48 */
/***/ (function(module, exports) {

var GOOD_LEAF_SIZE = 200

// :: class<T> A rope sequence is a persistent sequence data structure
// that supports appending, prepending, and slicing without doing a
// full copy. It is represented as a mostly-balanced tree.
var RopeSequence = function RopeSequence () {};

RopeSequence.prototype.append = function append (other) {
  if (!other.length) { return this }
  other = RopeSequence.from(other)

  return (!this.length && other) ||
    (other.length < GOOD_LEAF_SIZE && this.leafAppend(other)) ||
    (this.length < GOOD_LEAF_SIZE && other.leafPrepend(this)) ||
    this.appendInner(other)
};

// :: (union<[T], RopeSequence<T>>) → RopeSequence<T>
// Prepend an array or other rope to this one, returning a new rope.
RopeSequence.prototype.prepend = function prepend (other) {
  if (!other.length) { return this }
  return RopeSequence.from(other).append(this)
};

RopeSequence.prototype.appendInner = function appendInner (other) {
  return new Append(this, other)
};

// :: (?number, ?number) → RopeSequence<T>
// Create a rope repesenting a sub-sequence of this rope.
RopeSequence.prototype.slice = function slice (from, to) {
    if ( from === void 0 ) from = 0;
    if ( to === void 0 ) to = this.length;

  if (from >= to) { return RopeSequence.empty }
  return this.sliceInner(Math.max(0, from), Math.min(this.length, to))
};

// :: (number) → T
// Retrieve the element at the given position from this rope.
RopeSequence.prototype.get = function get (i) {
  if (i < 0 || i >= this.length) { return undefined }
  return this.getInner(i)
};

// :: ((element: T, index: number) → ?bool, ?number, ?number)
// Call the given function for each element between the given
// indices. This tends to be more efficient than looping over the
// indices and calling `get`, because it doesn't have to descend the
// tree for every element.
RopeSequence.prototype.forEach = function forEach (f, from, to) {
    if ( from === void 0 ) from = 0;
    if ( to === void 0 ) to = this.length;

  if (from <= to)
    { this.forEachInner(f, from, to, 0) }
  else
    { this.forEachInvertedInner(f, from, to, 0) }
};

// :: ((element: T, index: number) → U, ?number, ?number) → [U]
// Map the given functions over the elements of the rope, producing
// a flat array.
RopeSequence.prototype.map = function map (f, from, to) {
    if ( from === void 0 ) from = 0;
    if ( to === void 0 ) to = this.length;

  var result = []
  this.forEach(function (elt, i) { return result.push(f(elt, i)); }, from, to)
  return result
};

// :: (?union<[T], RopeSequence<T>>) → RopeSequence<T>
// Create a rope representing the given array, or return the rope
// itself if a rope was given.
RopeSequence.from = function from (values) {
  if (values instanceof RopeSequence) { return values }
  return values && values.length ? new Leaf(values) : RopeSequence.empty
};

var Leaf = (function (RopeSequence) {
  function Leaf(values) {
    RopeSequence.call(this)
    this.values = values
  }

  if ( RopeSequence ) Leaf.__proto__ = RopeSequence;
  Leaf.prototype = Object.create( RopeSequence && RopeSequence.prototype );
  Leaf.prototype.constructor = Leaf;

  var prototypeAccessors = { length: {},depth: {} };

  Leaf.prototype.flatten = function flatten () {
    return this.values
  };

  Leaf.prototype.sliceInner = function sliceInner (from, to) {
    if (from == 0 && to == this.length) { return this }
    return new Leaf(this.values.slice(from, to))
  };

  Leaf.prototype.getInner = function getInner (i) {
    return this.values[i]
  };

  Leaf.prototype.forEachInner = function forEachInner (f, from, to, start) {
    var this$1 = this;

    for (var i = from; i < to; i++)
      { if (f(this$1.values[i], start + i) === false) { return false } }
  };

  Leaf.prototype.forEachInvertedInner = function forEachInvertedInner (f, from, to, start) {
    var this$1 = this;

    for (var i = from - 1; i >= to; i--)
      { if (f(this$1.values[i], start + i) === false) { return false } }
  };

  Leaf.prototype.leafAppend = function leafAppend (other) {
    if (this.length + other.length <= GOOD_LEAF_SIZE)
      { return new Leaf(this.values.concat(other.flatten())) }
  };

  Leaf.prototype.leafPrepend = function leafPrepend (other) {
    if (this.length + other.length <= GOOD_LEAF_SIZE)
      { return new Leaf(other.flatten().concat(this.values)) }
  };

  prototypeAccessors.length.get = function () { return this.values.length };

  prototypeAccessors.depth.get = function () { return 0 };

  Object.defineProperties( Leaf.prototype, prototypeAccessors );

  return Leaf;
}(RopeSequence));

// :: RopeSequence
// The empty rope sequence.
RopeSequence.empty = new Leaf([])

var Append = (function (RopeSequence) {
  function Append(left, right) {
    RopeSequence.call(this)
    this.left = left
    this.right = right
    this.length = left.length + right.length
    this.depth = Math.max(left.depth, right.depth) + 1
  }

  if ( RopeSequence ) Append.__proto__ = RopeSequence;
  Append.prototype = Object.create( RopeSequence && RopeSequence.prototype );
  Append.prototype.constructor = Append;

  Append.prototype.flatten = function flatten () {
    return this.left.flatten().concat(this.right.flatten())
  };

  Append.prototype.getInner = function getInner (i) {
    return i < this.left.length ? this.left.get(i) : this.right.get(i - this.left.length)
  };

  Append.prototype.forEachInner = function forEachInner (f, from, to, start) {
    var leftLen = this.left.length
    if (from < leftLen &&
        this.left.forEachInner(f, from, Math.min(to, leftLen), start) === false)
      { return false }
    if (to > leftLen &&
        this.right.forEachInner(f, Math.max(from - leftLen, 0), Math.min(this.length, to) - leftLen, start + leftLen) === false)
      { return false }
  };

  Append.prototype.forEachInvertedInner = function forEachInvertedInner (f, from, to, start) {
    var leftLen = this.left.length
    if (from > leftLen &&
        this.right.forEachInvertedInner(f, from - leftLen, Math.max(to, leftLen) - leftLen, start + leftLen) === false)
      { return false }
    if (to < leftLen &&
        this.left.forEachInvertedInner(f, Math.min(from, leftLen), to, start) === false)
      { return false }
  };

  Append.prototype.sliceInner = function sliceInner (from, to) {
    if (from == 0 && to == this.length) { return this }
    var leftLen = this.left.length
    if (to <= leftLen) { return this.left.slice(from, to) }
    if (from >= leftLen) { return this.right.slice(from - leftLen, to - leftLen) }
    return this.left.slice(from, leftLen).append(this.right.slice(0, to - leftLen))
  };

  Append.prototype.leafAppend = function leafAppend (other) {
    var inner = this.right.leafAppend(other)
    if (inner) { return new Append(this.left, inner) }
  };

  Append.prototype.leafPrepend = function leafPrepend (other) {
    var inner = this.left.leafPrepend(other)
    if (inner) { return new Append(inner, this.right) }
  };

  Append.prototype.appendInner = function appendInner (other) {
    if (this.left.depth >= Math.max(this.right.depth, other.depth) + 1)
      { return new Append(this.left, new Append(this.right, other)) }
    return new Append(this, other)
  };

  return Append;
}(RopeSequence));

module.exports = RopeSequence


/***/ }),
/* 49 */
/***/ (function(module, exports) {

var base = {
  8: "Backspace",
  9: "Tab",
  10: "Enter",
  12: "NumLock",
  13: "Enter",
  16: "Shift",
  17: "Control",
  18: "Alt",
  20: "CapsLock",
  27: "Escape",
  32: " ",
  33: "PageUp",
  34: "PageDown",
  35: "End",
  36: "Home",
  37: "ArrowLeft",
  38: "ArrowUp",
  39: "ArrowRight",
  40: "ArrowDown",
  44: "PrintScreen",
  45: "Insert",
  46: "Delete",
  59: ";",
  61: "=",
  91: "Meta",
  92: "Meta",
  106: "*",
  107: "+",
  108: ",",
  109: "-",
  110: ".",
  111: "/",
  144: "NumLock",
  145: "ScrollLock",
  160: "Shift",
  161: "Shift",
  162: "Control",
  163: "Control",
  164: "Alt",
  165: "Alt",
  173: "-",
  186: ";",
  187: "=",
  188: ",",
  189: "-",
  190: ".",
  191: "/",
  192: "`",
  219: "[",
  220: "\\",
  221: "]",
  222: "'",
  229: "q"
}
var shift = {
  48: ")",
  49: "!",
  50: "@",
  51: "#",
  52: "$",
  53: "%",
  54: "^",
  55: "&",
  56: "*",
  57: "(",
  59: ";",
  61: "+",
  173: "_",
  186: ":",
  187: "+",
  188: "<",
  189: "_",
  190: ">",
  191: "?",
  192: "~",
  219: "{",
  220: "|",
  221: "}",
  222: "\"",
  229: "Q"
}

var chrome = typeof navigator != "undefined" && /Chrome\/(\d+)/.exec(navigator.userAgent)
var brokenModifierNames = chrome && +chrome[1] < 57

// Fill in the digit keys
for (var i = 0; i < 10; i++) base[48 + i] = base[96 + i] = String(i)

// The function keys
for (var i = 1; i <= 24; i++) base[i + 111] = "F" + i

// And the alphabetic keys
for (var i = 65; i <= 90; i++) {
  base[i] = String.fromCharCode(i + 32)
  shift[i] = String.fromCharCode(i)
}

// For each code that doesn't have a shift-equivalent, copy the base name
for (var code in base) if (!shift.hasOwnProperty(code)) shift[code] = base[code]

function keyName(event) {
  // Don't trust event.key in Chrome when there are modifiers until
  // they fix https://bugs.chromium.org/p/chromium/issues/detail?id=633838
  var name = ((!brokenModifierNames || !event.ctrlKey && !event.altKey && !event.metaKey) && event.key) ||
    (event.shiftKey ? shift : base)[event.keyCode] ||
    event.key || "Unidentified"
  // Edge sometimes produces wrong names (Issue #3)
  if (name == "Esc") name = "Escape"
  if (name == "Del") name = "Delete"
  // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/8860571/
  if (name == "Left") name = "ArrowLeft"
  if (name == "Up") name = "ArrowUp"
  if (name == "Right") name = "ArrowRight"
  if (name == "Down") name = "ArrowDown"
  return name
}

module.exports = keyName
keyName.base = base
keyName.shift = shift


/***/ }),
/* 50 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_prosemirror_view__ = __webpack_require__(29);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_prosemirror_view___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_prosemirror_view__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_0_prosemirror_view__, "EditorView")) __webpack_require__.d(__webpack_exports__, "EditorView", function() { return __WEBPACK_IMPORTED_MODULE_0_prosemirror_view__["EditorView"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_0_prosemirror_view__, "Decoration")) __webpack_require__.d(__webpack_exports__, "Decoration", function() { return __WEBPACK_IMPORTED_MODULE_0_prosemirror_view__["Decoration"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_0_prosemirror_view__, "DecorationSet")) __webpack_require__.d(__webpack_exports__, "DecorationSet", function() { return __WEBPACK_IMPORTED_MODULE_0_prosemirror_view__["DecorationSet"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prosemirror_state___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "EditorState")) __webpack_require__.d(__webpack_exports__, "EditorState", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["EditorState"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "Selection")) __webpack_require__.d(__webpack_exports__, "Selection", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["Selection"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "SelectionRange")) __webpack_require__.d(__webpack_exports__, "SelectionRange", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["SelectionRange"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "TextSelection")) __webpack_require__.d(__webpack_exports__, "TextSelection", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["TextSelection"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "NodeSelection")) __webpack_require__.d(__webpack_exports__, "NodeSelection", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["NodeSelection"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "AllSelection")) __webpack_require__.d(__webpack_exports__, "AllSelection", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["AllSelection"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "Transaction")) __webpack_require__.d(__webpack_exports__, "Transaction", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["Transaction"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "Plugin")) __webpack_require__.d(__webpack_exports__, "Plugin", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["Plugin"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_1_prosemirror_state__, "PluginKey")) __webpack_require__.d(__webpack_exports__, "PluginKey", function() { return __WEBPACK_IMPORTED_MODULE_1_prosemirror_state__["PluginKey"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prosemirror_keymap__ = __webpack_require__(27);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prosemirror_keymap___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_prosemirror_keymap__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_2_prosemirror_keymap__, "keymap")) __webpack_require__.d(__webpack_exports__, "keymap", function() { return __WEBPACK_IMPORTED_MODULE_2_prosemirror_keymap__["keymap"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_prosemirror_commands__ = __webpack_require__(24);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_prosemirror_commands___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_prosemirror_commands__);
/* harmony reexport (default from non-hamory) */ __webpack_require__.d(__webpack_exports__, "commands", function() { return __WEBPACK_IMPORTED_MODULE_3_prosemirror_commands___default.a; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_prosemirror_history__ = __webpack_require__(25);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_prosemirror_history___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_prosemirror_history__);
/* harmony reexport (default from non-hamory) */ __webpack_require__.d(__webpack_exports__, "history", function() { return __WEBPACK_IMPORTED_MODULE_4_prosemirror_history___default.a; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__, "findWrapping")) __webpack_require__.d(__webpack_exports__, "findWrapping", function() { return __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__["findWrapping"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__, "liftTarget")) __webpack_require__.d(__webpack_exports__, "liftTarget", function() { return __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__["liftTarget"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__, "canSplit")) __webpack_require__.d(__webpack_exports__, "canSplit", function() { return __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__["canSplit"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__, "canJoin")) __webpack_require__.d(__webpack_exports__, "canJoin", function() { return __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__["canJoin"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__, "ReplaceAroundStep")) __webpack_require__.d(__webpack_exports__, "ReplaceAroundStep", function() { return __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__["ReplaceAroundStep"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__, "ReplaceStep")) __webpack_require__.d(__webpack_exports__, "ReplaceStep", function() { return __WEBPACK_IMPORTED_MODULE_5_prosemirror_transform__["ReplaceStep"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__ = __webpack_require__(28);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__, "wrapInList")) __webpack_require__.d(__webpack_exports__, "wrapInList", function() { return __WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__["wrapInList"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__, "splitListItem")) __webpack_require__.d(__webpack_exports__, "splitListItem", function() { return __WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__["splitListItem"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__, "liftListItem")) __webpack_require__.d(__webpack_exports__, "liftListItem", function() { return __WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__["liftListItem"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__, "sinkListItem")) __webpack_require__.d(__webpack_exports__, "sinkListItem", function() { return __WEBPACK_IMPORTED_MODULE_6_prosemirror_schema_list__["sinkListItem"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__ = __webpack_require__(26);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__, "InputRule")) __webpack_require__.d(__webpack_exports__, "InputRule", function() { return __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__["InputRule"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__, "wrappingInputRule")) __webpack_require__.d(__webpack_exports__, "wrappingInputRule", function() { return __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__["wrappingInputRule"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__, "textblockTypeInputRule")) __webpack_require__.d(__webpack_exports__, "textblockTypeInputRule", function() { return __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__["textblockTypeInputRule"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__, "inputRules")) __webpack_require__.d(__webpack_exports__, "inputRules", function() { return __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__["inputRules"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__, "undoInputRule")) __webpack_require__.d(__webpack_exports__, "undoInputRule", function() { return __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__["undoInputRule"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__, "allInputRules")) __webpack_require__.d(__webpack_exports__, "allInputRules", function() { return __WEBPACK_IMPORTED_MODULE_7_prosemirror_inputrules__["allInputRules"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__ = __webpack_require__(0);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_prosemirror_model___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__);
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "Schema")) __webpack_require__.d(__webpack_exports__, "Schema", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["Schema"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "Node")) __webpack_require__.d(__webpack_exports__, "Node", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["Node"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "Mark")) __webpack_require__.d(__webpack_exports__, "Mark", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["Mark"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "ResolvedPos")) __webpack_require__.d(__webpack_exports__, "ResolvedPos", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["ResolvedPos"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "NodeRange")) __webpack_require__.d(__webpack_exports__, "NodeRange", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["NodeRange"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "Fragment")) __webpack_require__.d(__webpack_exports__, "Fragment", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["Fragment"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "Slice")) __webpack_require__.d(__webpack_exports__, "Slice", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["Slice"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "MarkType")) __webpack_require__.d(__webpack_exports__, "MarkType", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["MarkType"]; });
/* harmony reexport (binding) */ if(__webpack_require__.o(__WEBPACK_IMPORTED_MODULE_8_prosemirror_model__, "NodeType")) __webpack_require__.d(__webpack_exports__, "NodeType", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model__["NodeType"]; });
/* harmony reexport (default from non-hamory) */ __webpack_require__.d(__webpack_exports__, "model", function() { return __WEBPACK_IMPORTED_MODULE_8_prosemirror_model___default.a; });











/***/ })
/******/ ]);