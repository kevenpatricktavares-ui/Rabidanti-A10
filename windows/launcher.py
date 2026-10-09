import os
import subprocess
import tkinter as tk
from tkinter import messagebox
from pathlib import Path

RUNTIME_DIR = Path(os.environ.get("LOCALAPPDATA", str(Path.home()))) / "Rabidanti" / "windows-runtime"
START_SCRIPT = RUNTIME_DIR / "START-RABIDANTI-WINDOWS.ps1"

def start_rabidanti():
    if not START_SCRIPT.is_file():
        messagebox.showwarning(
            "Runtime Windows não encontrado",
            "O lançador foi instalado, mas o Core Windows ainda não está incluído.\n\n"
            "O pacote atual do repositório é específico para Android/Termux. "
            "Não é correto executá-lo como se fosse compatível com Windows.\n\n"
            "Quando existir um runtime Windows validado, coloque START-RABIDANTI-WINDOWS.ps1 em:\n"
            f"{RUNTIME_DIR}"
        )
        return
    try:
        subprocess.Popen(
            ["powershell.exe", "-NoProfile", "-ExecutionPolicy", "RemoteSigned", "-File", str(START_SCRIPT)],
            cwd=str(RUNTIME_DIR),
            creationflags=getattr(subprocess, "CREATE_NEW_PROCESS_GROUP", 0)
        )
        messagebox.showinfo("Rabidanti", "O comando de arranque foi enviado ao runtime Windows.")
    except Exception as exc:
        messagebox.showerror("Falha ao iniciar", f"Não foi possível iniciar o runtime.\n\n{exc}")

def show_status():
    exists = START_SCRIPT.is_file()
    status = "Runtime Windows encontrado." if exists else "Runtime Windows ainda não instalado/validado."
    messagebox.showinfo("Estado do Rabidanti", f"{status}\n\nLocal esperado:\n{RUNTIME_DIR}")

root = tk.Tk()
root.title("Rabidanti")
root.geometry("480x300")
root.minsize(440, 280)
root.configure(padx=24, pady=22)
tk.Label(root, text="RABIDANTI", font=("Segoe UI", 20, "bold")).pack(anchor="w")
tk.Label(root, text="Companion para Windows", font=("Segoe UI", 11)).pack(anchor="w", pady=(0, 18))
tk.Label(
    root,
    text="Este lançador verifica e inicia um runtime Windows separado. "
         "O runtime Android/Termux não é compatível com Windows por defeito.",
    wraplength=420, justify="left", font=("Segoe UI", 10)
).pack(anchor="w", pady=(0, 18))
tk.Button(root, text="Iniciar Rabidanti", command=start_rabidanti, height=2).pack(fill="x", pady=4)
tk.Button(root, text="Verificar estado", command=show_status, height=2).pack(fill="x", pady=4)
tk.Label(root, text="Versão inicial do lançador • Core Windows ainda por validar",
         font=("Segoe UI", 8), fg="#555555").pack(anchor="w", pady=(16, 0))
root.mainloop()
