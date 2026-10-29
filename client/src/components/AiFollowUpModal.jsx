import React, { useState } from 'react';
import { X, Sparkles, Copy, Check, Mail, MessageSquare, Linkedin, Calendar, Send } from 'lucide-react';
import { aiApi } from '../api/aiApi';

const CHANNELS = [
  { id: 'EMAIL', label: 'Email', icon: Mail },
  { id: 'WHATSAPP', label: 'WhatsApp', icon: MessageSquare },
  { id: 'LINKEDIN', label: 'LinkedIn', icon: Linkedin },
  { id: 'MEETING', label: 'Meeting Note', icon: Calendar },
];

const TONES = ['Professional', 'Friendly', 'Urgent', 'Consultative'];

export default function AiFollowUpModal({ isOpen, onClose, lead }) {
  const [channel, setChannel] = useState('EMAIL');
  const [tone, setTone] = useState('Professional');
  const [objective, setObjective] = useState('');
  const [customNotes, setCustomNotes] = useState('');
  const [generatedDraft, setGeneratedDraft] = useState(null);
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen || !lead) return null;

  const handleGenerate = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError('');
    setCopied(false);

    try {
      const res = await aiApi.generateFollowUp({
        leadId: lead.id,
        channel,
        tone,
        objective: objective.trim() || 'Follow up on previous communication',
        customNotes: customNotes.trim(),
      });

      if (res.success && res.data) {
        setGeneratedDraft(res.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate message draft.');
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = () => {
    if (!generatedDraft) return;
    const fullText = generatedDraft.subject
      ? `Subject: ${generatedDraft.subject}\n\n${generatedDraft.messageBody}`
      : generatedDraft.messageBody;
    navigator.clipboard.writeText(fullText);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-in fade-in overflow-y-auto">
      <div className="bg-white rounded-2xl max-w-2xl w-full shadow-2xl border border-slate-100 overflow-hidden my-8">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-gradient-to-r from-purple-50 via-indigo-50 to-white">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-purple-600 text-white flex items-center justify-center shadow-sm">
              <Sparkles className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-800">AI Outreach & Follow-up Generator</h3>
              <p className="text-xs text-slate-500">Generating for {lead.fullName} at {lead.company}</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-4">
          {/* Channel selector */}
          <div>
            <label className="block text-xs font-semibold text-slate-600 mb-1.5">Communication Channel</label>
            <div className="grid grid-cols-4 gap-2">
              {CHANNELS.map(ch => {
                const Icon = ch.icon;
                const active = channel === ch.id;
                return (
                  <button
                    key={ch.id}
                    type="button"
                    onClick={() => setChannel(ch.id)}
                    className={`flex items-center justify-center space-x-2 py-2 px-3 rounded-xl border text-xs font-medium transition ${
                      active
                        ? 'bg-purple-50 border-purple-500 text-purple-700 shadow-sm'
                        : 'border-slate-200 text-slate-600 hover:bg-slate-50'
                    }`}
                  >
                    <Icon className="w-3.5 h-3.5" />
                    <span>{ch.label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-600 mb-1">Tone of Voice</label>
              <select
                value={tone}
                onChange={(e) => setTone(e.target.value)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-xs focus:ring-2 focus:ring-purple-500 focus:border-purple-500 outline-none bg-white"
              >
                {TONES.map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-600 mb-1">Goal / Objective</label>
              <input
                type="text"
                value={objective}
                onChange={(e) => setObjective(e.target.value)}
                placeholder="e.g. Book 15-min discovery, follow up on proposal"
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-xs focus:ring-2 focus:ring-purple-500 focus:border-purple-500 outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-600 mb-1">Special Context / Custom Touchpoints</label>
            <input
              type="text"
              value={customNotes}
              onChange={(e) => setCustomNotes(e.target.value)}
              placeholder="e.g. Mention their recent expansion or previous meeting points"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-xs focus:ring-2 focus:ring-purple-500 focus:border-purple-500 outline-none"
            />
          </div>

          <div className="flex justify-end">
            <button
              type="button"
              onClick={handleGenerate}
              disabled={loading}
              className="flex items-center space-x-2 px-5 py-2 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 text-white text-xs font-semibold shadow-md shadow-purple-500/20 transition active:scale-95"
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>{loading ? 'AI Thinking & Drafting...' : generatedDraft ? 'Regenerate Draft' : 'Generate AI Draft'}</span>
            </button>
          </div>

          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 rounded-lg text-xs">
              {error}
            </div>
          )}

          {/* Generated Draft Output Area */}
          {generatedDraft && (
            <div className="mt-4 pt-4 border-t border-slate-200 space-y-3 animate-in fade-in">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <span className="text-xs font-bold text-slate-700 uppercase tracking-wider">AI Drafted Message</span>
                  <span className="text-[10px] bg-purple-100 text-purple-700 font-semibold px-2 py-0.5 rounded-full">
                    {generatedDraft.provider}
                  </span>
                </div>
                <button
                  type="button"
                  onClick={handleCopy}
                  className="flex items-center space-x-1.5 px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-medium transition"
                >
                  {copied ? <Check className="w-3.5 h-3.5 text-emerald-600" /> : <Copy className="w-3.5 h-3.5" />}
                  <span>{copied ? 'Copied!' : 'Copy to Clipboard'}</span>
                </button>
              </div>

              {generatedDraft.subject && (
                <div>
                  <span className="text-[11px] font-semibold text-slate-500 block mb-1">Subject:</span>
                  <input
                    type="text"
                    value={generatedDraft.subject}
                    onChange={(e) => setGeneratedDraft({ ...generatedDraft, subject: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-medium text-slate-800"
                  />
                </div>
              )}

              <div>
                <span className="text-[11px] font-semibold text-slate-500 block mb-1">Message Content (Editable):</span>
                <textarea
                  rows="6"
                  value={generatedDraft.messageBody}
                  onChange={(e) => setGeneratedDraft({ ...generatedDraft, messageBody: e.target.value })}
                  className="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-800 focus:bg-white focus:ring-2 focus:ring-purple-500 focus:border-purple-500 outline-none resize-none leading-relaxed"
                />
              </div>

              <p className="text-[11px] text-slate-400 italic">
                * Messages are drafts for human sales representatives to review, edit, and send manually.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
