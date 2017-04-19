import commonjs from 'rollup-plugin-commonjs';
import nodeResolve from 'rollup-plugin-node-resolve';
import json from 'rollup-plugin-json';
import builtins from 'rollup-plugin-node-builtins';

export default {
    entry: 'src/js/bundle.js',
    dest: 'src/js/bundle.pack.js',
    format: 'iife',
    moduleName: 'pm',
    plugins: [
        builtins(),
        json(),
        nodeResolve({
            jsnext: true,
            main: true
        }),
        commonjs({})
    ]
};