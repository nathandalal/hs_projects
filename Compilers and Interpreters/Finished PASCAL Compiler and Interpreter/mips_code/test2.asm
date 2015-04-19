	.text
	.globl main
	
main:
	la $t0, vara
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 9
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	la $t0, varb
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 10
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	la $t0, vara
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 11
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	la $t0, vara
	lw $v0, ($t0)
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 3
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	# now num1 in $t0 and num2 in $v0
	addu $v0, $t0, $v0 # adds and stores in $v0
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $ra, ($sp) # PUSH
	jal procfoo
	lw $ra, ($sp)
	addu $sp, $sp, 4 # POP
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	
	li $v0 10
	syscall	# halt
	
procfoo:
	li $t0, 0
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	la $t0, vara
	lw $v0, ($t0)
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	lw $v0, 12($sp)
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	lw $v0, 8($sp)
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	move $v0, $t0
	jr $ra # end of procfoo
	
	.data
	
vara:
	.word 0
varb:
	.word 0
nextln:
	.asciiz "\n"
