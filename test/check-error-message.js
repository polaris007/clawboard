const fs = require('fs');
const path = require('path');

function checkErrorMessages(rootDir) {
    const withError = [];
    const withoutError = [];
    
    function walkDir(dir) {
        const files = fs.readdirSync(dir);
        for (const file of files) {
            const filepath = path.join(dir, file);
            const stat = fs.statSync(filepath);
            
            if (stat.isDirectory()) {
                walkDir(filepath);
            } else if (file.endsWith('.jsonl') && !file.includes('.reset.') && !file.includes('.deleted.')) {
                try {
                    const content = fs.readFileSync(filepath, 'utf-8');
                    const lines = content.split('\n');
                    
                    lines.forEach((line, lineNum) => {
                        line = line.trim();
                        if (!line) return;
                        
                        try {
                            const record = JSON.parse(line);
                            if (record.type === 'message') {
                                const msg = record.message || {};
                                if (msg.role === 'assistant') {
                                    if ('errorMessage' in msg) {
                                        withError.push({
                                            file: file,
                                            line: lineNum + 1,
                                            errorMsg: msg.errorMessage ? msg.errorMessage.substring(0, 100) : null
                                        });
                                    } else {
                                        withoutError.push({
                                            file: file,
                                            line: lineNum + 1,
                                            hasStopReason: 'stopReason' in msg,
                                            stopReason: msg.stopReason
                                        });
                                    }
                                }
                            }
                        } catch (e) {
                            // Skip malformed lines
                        }
                    });
                } catch (e) {
                    console.error(`Error reading ${filepath}: ${e.message}`);
                }
            }
        }
    }
    
    walkDir(rootDir);
    return { withError, withoutError };
}

const root = 'G:\\Workplace\\github\\clawboard\\test\\session-transcript\\openclaw-logs';
const { withError, withoutError } = checkErrorMessages(root);

console.log('\n=== Summary ===');
console.log(`Assistant messages WITH errorMessage: ${withError.length}`);
console.log(`Assistant messages WITHOUT errorMessage: ${withoutError.length}`);

if (withError.length > 0) {
    console.log('\n=== Examples of messages with errorMessage ===');
    withError.slice(0, 5).forEach(item => {
        console.log(`File: ${item.file}, Line: ${item.line}`);
        console.log(`  Error: ${item.errorMsg}`);
        console.log();
    });
}

if (withoutError.length > 0) {
    console.log('\n=== Sample of messages without errorMessage ===');
    withoutError.slice(0, 5).forEach(item => {
        console.log(`File: ${item.file}, Line: ${item.line}`);
        console.log(`  Has stopReason: ${item.hasStopReason}, Stop: ${item.stopReason}`);
    });
}
